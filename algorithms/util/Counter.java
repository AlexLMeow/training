package util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @param <E> must be equals-immutable, otherwise behaviour is undefined.
 */
public class Counter<E> {
    public static class Entry<E> {
        public final E item;
        public final int count;
        private Entry(E item, int count) {
            this.item = item;
            this.count = count;
        }
        @Override
        public String toString() {
            return "{" + item + "} -> " + count;
        }
        public E item() {return item;} // help with method referencing
        public int count() {return count;} // help with method referencing
    }

    private Map<E, Integer> counts;
    private int totalCount = 0;

    // config flags

    public final boolean rememberZeroCounts; // if false, will not keep any entries with counts of 0 (sparse representation)
    public final boolean allowNegativeCounts; // if false, will lock count values to non-negative integers

    public Counter() {
        this.counts = new HashMap<>();
        this.allowNegativeCounts = false;
        this.rememberZeroCounts = false;
    }
    public Counter(Collection<E> items) {
        this();
        for (E item : items) { this.incrementCountFor(item); }
    }
    public Counter(E... items) {
        this(Arrays.asList(items));
    }


    public Counter(boolean allowNegativeCounts, boolean rememberZeroCounts) {
        this.counts = new HashMap<>();
        this.allowNegativeCounts = allowNegativeCounts;
        this.rememberZeroCounts = rememberZeroCounts;
    }
    public Counter(boolean allowNegativeCounts, boolean rememberZeroCounts, Collection<E> items) {
        this(allowNegativeCounts, rememberZeroCounts);
        for (E item : items) { this.incrementCountFor(item); }
    }
    public Counter(boolean allowNegativeCounts, boolean rememberZeroCounts, E... items) {
        this(allowNegativeCounts, rememberZeroCounts, Arrays.asList(items));
    }

    /**
     * Copies a given Counter, including config flags. Count states are not linked.
     */
    public Counter(Counter<E> cloneFrom) {
        this(cloneFrom.allowNegativeCounts, cloneFrom.rememberZeroCounts);
        // can directly copy internal state since same config constraints
        this.counts = new HashMap<>(cloneFrom.counts);
        this.totalCount = cloneFrom.totalCount;
    }
    public Counter(Counter<E> cloneFrom, boolean allowNegativeCounts, boolean rememberZeroCounts) {
        this(allowNegativeCounts, rememberZeroCounts);
        for (Entry<E> e : cloneFrom.getEntries()) {
            this.setCount(e.item, e.count);
        }
    }

    /**
     * Retrieve count of given item.
     * O(1) time.
     */
    public int getCountFor(E item) {
        Integer count = this.counts.get(item);
        if (count == null) { count = 0; }
        return count;
    }

    /**
     * Retrieve combined count of all items this counter has ever seen.
     * O(1) time.
     */
    public int getCombinedCount() { return totalCount; }

    /**
     * Increment count of given item.
     * O(1) time.
     * @return previous count (0 if no records).
     */
    public int incrementCountFor(E item) {
        return addToCount(item, 1);
    }

    /**
     * Decrement count of given item.
     * O(1) time.
     * @return previous count (0 if no records).
     */
    public int decrementCountFor(E item) {
        return addToCount(item, -1);
    }

    /**
     * Increases count for given item by delta value.
     * Delta can be negative.
     * O(1) time.
     * @return previous count (0 if no records).
     */
    public int addToCount(E item, int delta) {
        int newCount = getCountFor(item) + delta;
        return this.setCount(item, newCount);
    }

    /**
     * Sets count of an item to a specific value. Performs all update housekeeping:
     * - Updates {totalCount} memoization
     * - Binds count to non-negative values if not allowing negative values
     * - Trims entries if not remembering zero counts
     *
     * O(1) time.
     * @return previous count (0 if no records).
     */
    private int setCount(E item, int newCount) {
        if (!allowNegativeCounts) { newCount = Math.max(0, newCount); }
        Integer prevCount;
        if (!rememberZeroCounts && newCount == 0) {
            prevCount = this.counts.remove(item);
            if (prevCount == null) { prevCount = 0; }
            totalCount += newCount - prevCount;
        } else {
            prevCount = this.counts.put(item, newCount);
            if (prevCount == null) { prevCount = 0; }
            totalCount += newCount - prevCount;
        }
        return prevCount;
    }

    /**
     * Retrieve a snapshot list of counts for each item.
     * Changes to either this list or this counter instance will not propagate to each other.
     * If {rememberZeroCounts} is false, this list of entries will not contain items with a count of 0
     * even if the item was seen before.
     */
    public List<Entry<E>> getEntries() {
        return counts.entrySet().stream()
                .map(e -> new Entry<>(e.getKey(), e.getValue()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Retrieve a snapshot set of items tracked by the counter.
     * If {rememberZeroCounts} is false, this set of items will not contain items with a count of 0
     * even if the item was seen before.
     */
    public Set<E> getItemSet() {
        return new HashSet<>(counts.keySet());
    }

    public static void main(String... args) {

    }
}
