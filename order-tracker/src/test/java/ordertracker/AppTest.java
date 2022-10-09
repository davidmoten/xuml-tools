package ordertracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Scheduler;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import xuml.tools.util.database.DerbyUtil;

public class AppTest {

    private static final Logger log = LoggerFactory.getLogger(AppTest.class);

    private final Scheduler scheduler = Schedulers.from(Executors.newSingleThreadExecutor());

    @Before
    public void setup() {
        System.setProperty("org.jboss.logging.provider", "slf4j");
        DerbyUtil.disableDerbyLog();
        for (File file : new File("target").listFiles(f -> f.getName().startsWith("testdb.")))
            file.delete();
        try (Connection c = DriverManager.getConnection("jdbc:hsqldb:file:target/testdb", "sa",
                "")) {
            c.prepareStatement("create schema ORDERTRACKER authorization sa").execute();
        } catch (SQLException e) {
            log.warn(e.getMessage());
        }
        App.startup();
    }

    @After
    public void shutdown() {
        App.shutdown();
    }

    @Test
    public void testEventService() throws InterruptedException {
        // TODO move this test to xuml-model-compiler-test
        final CountDownLatch latch = new CountDownLatch(1);
        EventService.instance().events().subscribeOn(scheduler).subscribe(event -> {
            log.info("event=" + event);
            latch.countDown();
        });

        Order.create(new Order.Events.Create("1", "test order", "canberra", "sydney",
                "fred@yahoo.com", "joey@gmail.com", 3, "created"));
        assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testDeliverySequence() throws InterruptedException {
        final List<String> states = new CopyOnWriteArrayList<>();
        List<String> expectedStates = toList(Order.State.PREPARING, Order.State.READY_FOR_DISPATCH,
                Order.State.COURIER_ASSIGNED, Order.State.IN_TRANSIT, Order.State.IN_TRANSIT,
                Order.State.READY_FOR_DELIVERY, Order.State.DELIVERING, Order.State.DELIVERED);
        final List<CountDownLatch> latches = new CopyOnWriteArrayList<>();
        for (@SuppressWarnings("unused")
        String state : expectedStates)
            latches.add(new CountDownLatch(1));
        Subscriber<String> subscriber = createSubscriber(states, latches);
        EventService.instance().events().take(expectedStates.size()).subscribeOn(scheduler)
                .subscribe(subscriber);

        Order order;
        synchronized (this) {
            // note for using with h2 enforce happens-before by creating order
            // using depot
            Depot depot = Depot.create(new Depot.Events.Create("2", "Bungendore", -35.0, 142.0));
            order = Order.create(new Order.Events.Create(depot.getId(), "test order", "canberra",
                    "sydney", "fred@yahoo.com", "joey@gmail.com", 3, "created"));
        }
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
        subscriber.unsubscribe();
    }

    // keep timeout quite large so that freebie CI servers don't fail when they
    // are under load
    @Test
    public void testDeliverySequenceWithRetries() throws InterruptedException {
        final List<String> states = new CopyOnWriteArrayList<>();
        List<String> expectedStates = toList(Order.State.PREPARING, Order.State.READY_FOR_DISPATCH,
                Order.State.COURIER_ASSIGNED, Order.State.IN_TRANSIT, Order.State.IN_TRANSIT,
                Order.State.READY_FOR_DELIVERY, Order.State.DELIVERING, Order.State.DELIVERY_FAILED,
                Order.State.AWAITING_NEXT_DELIVERY_ATTEMPT, Order.State.READY_FOR_DELIVERY,
                Order.State.DELIVERING, Order.State.DELIVERY_FAILED,
                Order.State.AWAITING_NEXT_DELIVERY_ATTEMPT, Order.State.READY_FOR_DELIVERY,
                Order.State.DELIVERING, Order.State.DELIVERY_FAILED, Order.State.HELD_FOR_PICKUP,
                Order.State.DELIVERED);
        final List<CountDownLatch> latches = new ArrayList<>();
        for (@SuppressWarnings("unused")
        String state : expectedStates)
            latches.add(new CountDownLatch(1));
        Subscriber<String> subscriber = createSubscriber(states, latches);
        EventService.instance().events().take(expectedStates.size()).subscribeOn(scheduler)
                .subscribe(subscriber);
        Order order;
        synchronized (this) {
            // prevent reordering with latch checks
            order = Order.create(new Order.Events.Create("3", "test order", "canberra", "sydney",
                    "fred@yahoo.com", "joey@gmail.com", 3, "created"));
            Depot.create(new Depot.Events.Create("3", "Bungendore", -35.0, 142.0));
        }
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
        order.signal(new Order.Events.DeliveryFailed());
        checkLatch(latches, expectedStates, states, count++);
        // order.signal(new Order.Events.DeliverAgain());
        // checkLatch(latches, expectedStates, states, count++);
        // order.signal(new Order.Events.DeliveryFailed());
        // checkLatch(latches, expectedStates, states, count++);
        // order.signal(new Order.Events.DeliveredByPickup());
        // checkLatch(latches, expectedStates, states, count++);
        subscriber.unsubscribe();
    }

    private static List<String> toList(Order.State... states) {
        List<String> list = new ArrayList<String>();
        for (Order.State state : states)
            list.add(state.toString());
        return list;
    }

    private static synchronized void checkLatch(List<CountDownLatch> latches,
            List<String> expectedStates, List<String> states, int index)
                    throws InterruptedException {
        log.info(
                "waiting for latch " + index + " to detect state " + expectedStates.get(index));
        assertTrue("latch timeout, states="+ states, latches.get(index).await(10, TimeUnit.SECONDS));
        log.info("latch obtained for " + index);
        assertEquals(expectedStates.subList(0, index + 1), states.subList(0, index + 1));
    }

    private Subscriber<String> createSubscriber(final List<String> states,
            final List<CountDownLatch> latches) {
        return new Subscriber<String>() {

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
                log.info(Thread.currentThread().getName() + " - " + state);
                states.add(state);
                latches.get(count).countDown();
                count++;
            }
        };
    }

}