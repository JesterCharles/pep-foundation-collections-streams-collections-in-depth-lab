package routing;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * The warehouse's order-routing registry. It records incoming orders, queues them at their packing
 * stations, dispatches orders and undoes a dispatch, and answers the floor manager's reports.
 *
 * Tasks 2-6 are in this file. Each has a TODO comment; Task 3 has one in each of its two
 * methods. The fields, the methods marked "Given" and main are already complete.
 */
public class RoutingRegistry {

    /** Every registered order by key, in arrival order: LinkedHashMap keeps insertion order. */
    private final Map<OrderKey, Order> orders = new LinkedHashMap<>();

    /** One first-in-first-out queue of waiting orders per station. */
    private final Map<String, Deque<Order>> stationQueues = new HashMap<>();

    /** Dispatched orders, used as a last-in-first-out stack: the newest dispatch is on top. */
    private final Deque<Order> dispatched = new ArrayDeque<>();

    /** Given. Stores the order under its key; an order with an equal key replaces it. Returns the replaced order, or null. */
    public Order register(Order order) {
        return orders.put(order.key(), order);
    }

    /** Given. Looks an order up by channel and number; null when there is none. Works once Task 1 is done. */
    public Order find(String channel, int number) {
        return orders.get(new OrderKey(channel, number));
    }

    /** Given. The registered orders in arrival order, as an unmodifiable copy. */
    public List<Order> orders() {
        return List.copyOf(orders.values());
    }

    /** Given. The orders waiting at a station, oldest (next to dispatch) first, as an unmodifiable copy. */
    public List<Order> waiting(String station) {
        Deque<Order> queue = stationQueues.get(station);
        return queue == null ? List.of() : List.copyOf(queue);
    }

    /**
     * Task 2: removes every registered order whose sku is in discontinuedSkus and returns how many
     * orders it removed.
     */
    public int dropDiscontinued(Set<String> discontinuedSkus) {
        // TODO Task 2: this loop removes entries from the map while a for-each loop is walking it,
        // which throws ConcurrentModificationException. Remove through the collection itself
        // instead: removeIf on one of the map's views, or an explicit Iterator and its remove method.
        int before = orders.size();
        for (OrderKey key : orders.keySet()) {
            if (discontinuedSkus.contains(orders.get(key).sku())) {
                orders.remove(key);
            }
        }
        return before - orders.size();
    }

    /** Task 3, part 1: adds the order to the back of its station's queue, creating that queue the first time the station appears. */
    public void queue(Order order) {
        // TODO Task 3: find the station's queue in stationQueues, creating an empty ArrayDeque the
        // first time a station appears (computeIfAbsent does both), and add the order at its back.
    }

    /**
     * Task 3, part 2: takes the oldest waiting order off the station's queue, pushes it onto the
     * dispatched stack and returns it. Returns null, and never throws, when the station is unknown or
     * nothing is waiting there.
     */
    public Order dispatchNext(String station) {
        // TODO Task 3: the station's queue may be missing (null) or empty. Take its oldest order
        // with the Deque method that returns null instead of throwing, and record a non-null
        // result on the dispatched stack before returning it.
        return null;
    }

    /**
     * Task 4: undoes the most recent dispatch. Takes the newest order off the dispatched stack and
     * puts it back at the head of its station's queue, so that station dispatches it next. Returns
     * that order, or null when nothing has been dispatched.
     */
    public Order undoLastDispatch() {
        // TODO Task 4: return null when nothing has been dispatched (the stack method that removes
        // the top throws on an empty deque). Otherwise take the newest dispatch off the stack and
        // add it at the head of its station's queue, the end the queue takes from.
        return null;
    }

    /**
     * Task 5: the orders whose sku is on hold, in arrival order.
     *
     * Big-O before (nested loops): O(?)    Big-O after (HashSet lookup): O(?)
     */
    public List<Order> heldOrders(List<String> heldSkus) {
        // TODO Task 5: this is correct, but it reads the whole held list again for every order:
        // n orders times m held skus. Read heldSkus once into a hash-based set, then look each
        // order's sku up in that set, and fill in the two Big-O labels above (n orders, m held skus).
        List<Order> held = new ArrayList<>();
        for (Order order : orders.values()) {
            for (String sku : heldSkus) {
                if (sku.equals(order.sku())) {
                    held.add(order);
                    break;
                }
            }
        }
        return held;
    }

    /** Task 6: the floor report, total units per station over all registered orders, stations in A to Z order. */
    public Map<String, Integer> unitsByStation() {
        // TODO Task 6: the totals are right, but HashMap makes no ordering guarantee, so the report
        // lists stations in a jumbled order. Use the Map implementation that keeps its keys sorted,
        // and replace the getOrDefault-and-put line with one call of the map's merge method.
        Map<String, Integer> units = new HashMap<>();
        for (Order order : orders.values()) {
            units.put(order.station(), units.getOrDefault(order.station(), 0) + order.quantity());
        }
        return units;
    }

    /** Given. The keys of some orders, for printing. */
    static List<OrderKey> keys(Collection<Order> list) {
        List<OrderKey> keys = new ArrayList<>();
        for (Order order : list) {
            keys.add(order.key());
        }
        return keys;
    }

    /** Given. A short demo run of the registry; run it with mvn -q -o compile exec:java. */
    public static void main(String[] args) {
        RoutingRegistry registry = new RoutingRegistry();
        List<Order> arrivals = List.of(
                new Order("web", 1001, "mug", 2, "B2"),
                new Order("phone", 1001, "lamp", 1, "A1"),
                new Order("web", 1002, "kettle", 1, "B2"),
                new Order("web", 1003, "mug", 4, "C3"),
                new Order("phone", 1002, "rug", 1, "A1"));
        for (Order order : arrivals) {
            registry.register(order);
            registry.queue(order);
        }
        Order kettle = registry.find("web", 1002);
        System.out.println("Registered " + registry.orders().size() + " orders; web-1002 is "
                + (kettle == null ? "not found" : kettle.sku() + " at " + kettle.station()));
        Order sent = registry.dispatchNext("B2");
        System.out.println("Dispatched from B2: " + (sent == null ? "nothing" : sent.key()));
        Order undone = registry.undoLastDispatch();
        System.out.println("Undo: " + (undone == null ? "nothing" : undone.key())
                + "; B2 next: " + keys(registry.waiting("B2")));
        System.out.println("Held (mug): " + keys(registry.heldOrders(List.of("mug"))));
        System.out.println("Units by station: " + registry.unitsByStation());
        int dropped = registry.dropDiscontinued(Set.of("rug"));
        System.out.println("Dropped discontinued (rug): " + dropped + "; " + registry.orders().size() + " orders left");
    }
}
