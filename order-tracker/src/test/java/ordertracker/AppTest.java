package ordertracker;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import rx.Observer;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import xuml.tools.util.database.DerbyUtil;

public class AppTest {

	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();
		App.startup();
	}

	@Test
	public void testEventService() throws InterruptedException {
		// TODO move this test to xuml-model-compiler-test
		final CountDownLatch latch = new CountDownLatch(1);
		EventService.instance().events().subscribeOn(Schedulers.newThread())
				.subscribe(new Action1<String>() {
					@Override
					public void call(String event) {
						latch.countDown();
					}
				});
		Order.create(new Order.Events.Create("1", "test order", "canberra",
				"sydney", "fred@yahoo.com", "joey@gmail.com", 3, "created"));
		latch.await(5000, TimeUnit.MILLISECONDS);
	}

	@Test
	public void testDeliverySequence() throws InterruptedException {
		final List<String> states = new ArrayList<>();
		List<String> expectedStates = Arrays.asList(
				Order.State.PREPARING.toString(),
				Order.State.READY_FOR_DISPATCH.toString(),
				Order.State.COURIER_ASSIGNED.toString(),
				Order.State.IN_TRANSIT.toString(),
				Order.State.IN_TRANSIT.toString(),
				Order.State.READY_FOR_DELIVERY.toString(),
				Order.State.DELIVERING.toString(),
				Order.State.DELIVERED.toString());
		final List<CountDownLatch> latches = new ArrayList<>();
		for (String state : expectedStates)
			latches.add(new CountDownLatch(1));
		EventService.instance().events().take(expectedStates.size())
				.subscribeOn(Schedulers.io())
				.subscribe(createObserver(states, latches));

		Depot.create(new Depot.Events.Create("2", "Bungendore", -35.0, 142.0));
		Order order = Order.create(new Order.Events.Create("2", "test order",
				"canberra", "sydney", "fred@yahoo.com", "joey@gmail.com", 3,
				"created"));
		int count = 0;
		checkLatch(latches, expectedStates, states, count++);
		order.signal(new Order.Events.Send());
		checkLatch(latches, expectedStates, states, count++);
		order.signal(new Order.Events.Assign());
		checkLatch(latches, expectedStates, states, count++);
		order.signal(new Order.Events.PickedUp());
		checkLatch(latches, expectedStates, states, count++);
		order.signal(new Order.Events.ArrivedDepot("2"));
		checkLatch(latches, expectedStates, states, count++);
		order.signal(new Order.Events.ArrivedFinalDepot("2"));
		checkLatch(latches, expectedStates, states, count++);
		order.signal(new Order.Events.Delivering());
		checkLatch(latches, expectedStates, states, count++);
		order.signal(new Order.Events.Delivered());
		checkLatch(latches, expectedStates, states, count++);
	}

	// keep timeout quite large so that freebie CI servers don't fail when they
	// are under load
	@Test
	public void testDeliverySequenceWithRetries() throws InterruptedException {
		final List<String> states = new ArrayList<>();
		List<String> expectedStates = Arrays.asList(
				Order.State.PREPARING.toString(),
				Order.State.READY_FOR_DISPATCH.toString(),
				Order.State.COURIER_ASSIGNED.toString(),
				Order.State.IN_TRANSIT.toString(),
				Order.State.IN_TRANSIT.toString(),
				Order.State.READY_FOR_DELIVERY.toString(),
				Order.State.DELIVERING.toString(),
				Order.State.DELIVERY_FAILED.toString(),
				Order.State.AWAITING_NEXT_DELIVERY_ATTEMPT.toString(),
				Order.State.READY_FOR_DELIVERY.toString(),
				Order.State.DELIVERING.toString(),
				Order.State.DELIVERY_FAILED.toString(),
				Order.State.AWAITING_NEXT_DELIVERY_ATTEMPT.toString(),
				Order.State.READY_FOR_DELIVERY.toString(),
				Order.State.DELIVERING.toString(),
				Order.State.DELIVERY_FAILED.toString(),
				Order.State.HELD_FOR_PICKUP.toString(),
				Order.State.DELIVERED.toString());
		final List<CountDownLatch> latches = new ArrayList<>();
		for (String state : expectedStates)
			latches.add(new CountDownLatch(1));
		EventService.instance().events().take(expectedStates.size())
				.subscribeOn(Schedulers.io())
				.subscribe(createObserver(states, latches));

		Order order = Order.create(new Order.Events.Create("3", "test order",
				"canberra", "sydney", "fred@yahoo.com", "joey@gmail.com", 3,
				"created"));

		Depot.create(new Depot.Events.Create("3", "Bungendore", -35.0, 142.0));
		int count = 0;
		checkLatch(latches, expectedStates, states, count++);
		order.signal(new Order.Events.Send());
		checkLatch(latches, expectedStates, states, count++);
		order.signal(new Order.Events.Assign());
		checkLatch(latches, expectedStates, states, count++);
		order.signal(new Order.Events.PickedUp());
		checkLatch(latches, expectedStates, states, count++);
		order.signal(new Order.Events.ArrivedDepot("3"));
		checkLatch(latches, expectedStates, states, count++);
		order.signal(new Order.Events.ArrivedFinalDepot("3"));
		checkLatch(latches, expectedStates, states, count++);
		order.signal(new Order.Events.Delivering());
		checkLatch(latches, expectedStates, states, count++);
		order.signal(new Order.Events.DeliveryFailed());
		checkLatch(latches, expectedStates, states, count++);
		order.signal(new Order.Events.DeliverAgain());
		checkLatch(latches, expectedStates, states, count++);
		// order.signal(new Order.Events.DeliveryFailed());
		// checkLatch(latches, expectedStates, states, count++);
		// order.signal(new Order.Events.DeliverAgain());
		// checkLatch(latches, expectedStates, states, count++);
		// order.signal(new Order.Events.DeliveryFailed());
		// checkLatch(latches, expectedStates, states, count++);
		// order.signal(new Order.Events.DeliveredByPickup());
		// checkLatch(latches, expectedStates, states, count++);
	}

	private static void checkLatch(List<CountDownLatch> latches,
			List<String> expectedStates, List<String> states, int index)
			throws InterruptedException {
		System.out.println("waiting for latch " + index + " to detect state "
				+ expectedStates.get(index));
		latches.get(index).await(30000, TimeUnit.MILLISECONDS);
		System.out.println("latch obtained for " + index);
		assertEquals(expectedStates.subList(0, index + 1), states);
	}

	private Observer<String> createObserver(final List<String> states,
			final List<CountDownLatch> latches) {
		return new Observer<String>() {

			int count = 0;

			@Override
			public void onCompleted() {
			}

			@Override
			public void onError(Throwable e) {
				throw new RuntimeException(e);
			}

			@Override
			public void onNext(String state) {
				states.add(state);
				latches.get(count).countDown();
				count++;
			}
		};
	}

	@AfterClass
	public static void shutdown() {
		App.shutdown();
	}

}