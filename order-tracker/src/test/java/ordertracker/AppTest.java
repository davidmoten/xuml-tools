package ordertracker;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ordertracker.Order.State;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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
		//TODO move this test to xuml-model-compiler-test
		final CountDownLatch latch = new CountDownLatch(1);
		EventService.instance().events().subscribeOn(Schedulers.newThread()).subscribe(new Action1<String>() {
			@Override
			public void call(String event) {
				latch.countDown();
			}});
		Context.create(Order.class, new Order.Events.Create("1", "test order", "canberra", "sydney", "fred@yahoo.com", "joey@gmail.com", 3, "created"));
		latch.await(5000,TimeUnit.MILLISECONDS);
	}
	
	@Test
	public void testDeliverySequence() throws InterruptedException {
		Order order = Context.create(Order.class, new Order.Events.Create("2", "test order", "canberra", "sydney", "fred@yahoo.com", "joey@gmail.com", 3, "created"));
		Context.create(Depot.class, new Depot.Events.Create("2", "bungendore", -35.0, 142.0));
		checkState(Order.State.PREPARING);
		order.signal(new Order.Events.Send());
		checkState(Order.State.READY_FOR_DISPATCH);
		order.signal(new Order.Events.Assign());
		checkState(Order.State.COURIER_ASSIGNED);
		order.signal(new Order.Events.PickedUp());
		checkState(Order.State.IN_TRANSIT);
		order.signal(new Order.Events.ArrivedDepot("2"));
		checkState(Order.State.IN_TRANSIT);
		
	}

	private void checkState(State state) throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(100);
		assertEquals(state.toString(), Order.find("2").getState());
	}

	@AfterClass
	public static void shutdown() {
		App.shutdown();
	}

	
}