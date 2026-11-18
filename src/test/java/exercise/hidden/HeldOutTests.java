package exercise.hidden;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import exercise.Order;
import exercise.RoutingRegistry;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Held-out tests: graded on submit, never shown to the learner. Edge cases for Tasks 1, 3 and 4. */
class HeldOutTests {

    private static Order order(String channel, int number, String sku, int quantity, String station) {
        return new Order(channel, number, sku, quantity, station);
    }

    /** Five arrivals over three stations; web-1001 and phone-1001 share a number but not a channel. */
    private static RoutingRegistry floor() {
        RoutingRegistry registry = new RoutingRegistry();
        for (Order o : List.of(
                order("web", 1001, "mug", 2, "B2"),
                order("phone", 1001, "lamp", 1, "A1"),
                order("web", 1002, "kettle", 1, "B2"),
                order("web", 1003, "mug", 4, "C3"),
                order("phone", 1002, "rug", 1, "A1"))) {
            registry.register(o);
        }
        return registry;
    }

    private static List<String> keys(List<Order> orders) {
        List<String> keys = new ArrayList<>();
        for (Order o : orders) {
            keys.add(o.key().toString());
        }
        return keys;
    }

    @Test
    void task1MissingKeyAndReplacement() {
        RoutingRegistry registry = floor();
        assertNull(registry.find("phone", 1003), "Task 1: find(\"phone\", 1003) must return null: there is no such order");
        registry.register(order("web", 1002, "kettle", 2, "B2"));
        assertEquals(5, registry.orders().size(), "Task 1: replacing web-1002 must keep 5 orders, not add a sixth");
    }

    @Test
    void task3UnknownStationAndOtherQueues() {
        RoutingRegistry registry = new RoutingRegistry();
        registry.queue(order("web", 1, "mug", 1, "B2"));
        registry.queue(order("web", 2, "lamp", 1, "B2"));
        registry.queue(order("web", 3, "rug", 1, "A1"));
        registry.dispatchNext("B2");
        registry.dispatchNext("B2");
        Order[] empty = new Order[1];
        assertDoesNotThrow(() -> empty[0] = registry.dispatchNext("Z9"),
                "Task 3: dispatchNext on a station that never had a queue must return null, not throw");
        assertNull(empty[0], "Task 3: dispatchNext(Z9) must return null for an unknown station");
        assertEquals(List.of("web-3"), keys(registry.waiting("A1")), "Task 3: dispatching from B2 must leave A1's queue alone");
    }

    @Test
    void task4UndoneOrderGoesBackToTheHead() {
        RoutingRegistry registry = new RoutingRegistry();
        Order a = order("web", 1, "mug", 1, "B2");
        Order b = order("web", 2, "lamp", 1, "B2");
        Order c = order("web", 3, "rug", 1, "A1");
        for (Order o : List.of(a, b, c)) {
            registry.queue(o);
        }
        registry.dispatchNext("B2");
        registry.dispatchNext("A1");
        registry.undoLastDispatch();
        registry.undoLastDispatch();
        assertEquals(List.of("web-1", "web-2"), keys(registry.waiting("B2")),
                "Task 4: the undone web-1 must be back at the head of B2's queue, ahead of web-2 (needs Task 3 too)");
        assertSame(a, registry.dispatchNext("B2"), "Task 4: after the undo, B2 must dispatch web-1 next again (needs Task 3 too)");
    }
}
