package routing;

import java.util.Objects;

/**
 * Identifies one order. The same number can exist on two channels (web-1001 and phone-1001 are
 * different orders), so the channel and the number together form the key.
 *
 * Task 1 is in this file.
 */
public final class OrderKey {
    private final String channel;
    private final int number;

    public OrderKey(String channel, int number) {
        this.channel = channel;
        this.number = number;
    }

    public String channel() {
        return channel;
    }

    public int number() {
        return number;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OrderKey that)) {
            return false;
        }
        return number == that.number && Objects.equals(channel, that.channel);
    }

    // TODO Task 1: find("web", 1002) returns null for an order that was registered a moment ago,
    // and a HashSet keeps two OrderKeys that equals says are the same. equals above is correct.
    // Work out which other method HashMap and HashSet call on a key before they ever call equals,
    // and add the override that makes this class keep its side of the equals contract.

    @Override
    public String toString() {
        return channel + "-" + number;
    }
}
