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

	// keep timeout quite large so that freebie CI servers don't fail when they
	// are under load
	@Test(timeout = 600000)
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
		final CountDownLatch latch = new CountDownLatch(1);
		EventService.instance().events().take(expectedStates.size())
				.subscribeOn(Schedulers.io())
				.subscribe(createObserver(states, latch));

		Order order = Order.create(new Order.Events.Create("2", "test order",
				"canberra", "sydney", "fred@yahoo.com", "joey@gmail.com", 3,
				"created"));

		Depot.create(new Depot.Events.Create("2", "Bungendore", -35.0, 142.0));
		order.signal(new Order.Events.Send());
		order.signal(new Order.Events.Assign());
		order.signal(new Order.Events.PickedUp());
		order.signal(new Order.Events.ArrivedDepot("2"));
		order.signal(new Order.Events.ArrivedFinalDepot("2"));
		order.signal(new Order.Events.Delivering());
		order.signal(new Order.Events.Delivered());
		latch.await();
		assertEquals(expectedStates, states);
	}

	// keep timeout quite large so that freebie CI servers don't fail when they
	// are under load
	// @Test(timeout = 6000)
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
				Order.State.DELIVERING.toString(),
				Order.State.DELIVERY_FAILED.toString(),
				Order.State.AWAITING_NEXT_DELIVERY_ATTEMPT.toString(),
				Order.State.DELIVERING.toString(),
				Order.State.DELIVERY_FAILED.toString(),
				Order.State.HELD_FOR_PICKUP.toString(),
				Order.State.DELIVERED.toString());
		final CountDownLatch latch = new CountDownLatch(1);
		EventService.instance().events().take(expectedStates.size())
				.subscribeOn(Schedulers.io())
				.subscribe(createObserver(states, latch));

		Order order = Order.create(new Order.Events.Create("3", "test order",
				"canberra", "sydney", "fred@yahoo.com", "joey@gmail.com", 3,
				"created"));

		Depot.create(new Depot.Events.Create("3", "Bungendore", -35.0, 142.0));
		order.signal(new Order.Events.Send());
		order.signal(new Order.Events.Assign());
		order.signal(new Order.Events.PickedUp());
		order.signal(new Order.Events.ArrivedDepot("2"));
		order.signal(new Order.Events.ArrivedFinalDepot("2"));
		order.signal(new Order.Events.Delivering());
		order.signal(new Order.Events.DeliveryFailed());
		order.signal(new Order.Events.DeliverAgain());
		order.signal(new Order.Events.DeliveryFailed());
		order.signal(new Order.Events.DeliverAgain());
		order.signal(new Order.Events.DeliveryFailed());
		order.signal(new Order.Events.DeliveredByPickup());
		latch.await();
		assertEquals(expectedStates, states);
	}

	private Observer<String> createObserver(final List<String> states,
			final CountDownLatch latch) {
		return new Observer<String>() {

			@Override
			public void onCompleted() {
				latch.countDown();
			}

			@Override
			public void onError(Throwable e) {
				throw new RuntimeException(e);
			}

			@Override
			public void onNext(String state) {
				states.add(state);
			}
		};
	}

	@AfterClass
	public static void shutdown() {
		App.shutdown();
	}

}