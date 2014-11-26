package ordertracker;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

	@AfterClass
	public static void shutdown() {
		App.shutdown();
	}

	
}