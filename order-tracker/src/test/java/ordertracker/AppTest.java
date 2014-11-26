package ordertracker;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ordertracker.Order.State;

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
		Context.create(Order.class, new Order.Events.Create("1", "test order",
				"canberra", "sydney", "fred@yahoo.com", "joey@gmail.com", 3,
				"created"));
		latch.await(5000, TimeUnit.MILLISECONDS);
	}

	@Test(timeout = 10000)
	public void testDeliverySequence() throws InterruptedException {
		final List<String> states = new ArrayList<>();
		final CountDownLatch latch = new CountDownLatch(1);
		EventService.instance().events().take(5).subscribeOn(Schedulers.io())
				.subscribe(new Observer<String>() {

					@Override
					public void onCompleted() {
						latch.countDown();
					}

					@Override
					public void onError(Throwable e) {

					}

					@Override
					public void onNext(String state) {
						states.add(state);
					}
				});

		Order order = Context.create(Order.class, new Order.Events.Create("2",
				"test order", "canberra", "sydney", "fred@yahoo.com",
				"joey@gmail.com", 3, "created"));

		Context.create(Depot.class, new Depot.Events.Create("2", "Bungendore",
				-35.0, 142.0));
		order.signal(new Order.Events.Send());
		order.signal(new Order.Events.Assign());
		order.signal(new Order.Events.PickedUp());
		order.signal(new Order.Events.ArrivedDepot("2"));
		latch.await();
		assertEquals(Arrays.asList("PREPARING", "READY_FOR_DISPATCH",
				"COURIER_ASSIGNED", "IN_TRANSIT", "IN_TRANSIT"), states);
	}

	private void checkState(State state) throws InterruptedException {
		while (!state.toString().equals(Order.find("2").getState()))
			TimeUnit.MILLISECONDS.sleep(100);
	}

	@AfterClass
	public static void shutdown() {
		App.shutdown();
	}

}