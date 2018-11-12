package sequences;

import util.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/** Google Code Jam Kickstart round G 2018
 *
 * There are N classes (indexed 1 to N)
 * The test scores in every class {i} forms a sequence of consecutive integers: L[i] to R[i]
 * After combining all scores in all classes, the teacher has Q questions (indexed 1 to Q) to ask:
 * Q_i: what is the K[i]th highest score in the whole school?
 *
 * Output:
 * Sum of (S[i] * i) // i = 1 to Q
 *
 * Input:
 * We will generate L, R, and K arrays from the following relations and given base cases:
 *
 * BASE CASE VALUES GIVEN:
 * X[1], X[2], A[1], B[1], C[1], M[1]
 * Y[1], Y[2], A[2], B[2], C[2], M[2]
 * Z[1], Z[2], A[3], B[3], C[3], M[3]
 *
 * INTERMEDIATE SEQUENCES:
 * X[i] = A[1]*X[i-1] + B[1]*X[i-2] + C[1] % M[1] // i = 3 to N
 * Y[i] = A[2]*Y[i-1] + B[2]*Y[i-2] + C[2] % M[2] // i = 3 to N
 * Z[i] = A[3]*Z[i-1] + B[3]*Z[i-2] + C[3] % M[3] // i = 3 to Q
 *
 * USEFUL VALUES:
 * L[i] = min(X[i], Y[i]) + 1 // i up to N
 * R[i] = min(X[i], Y[i]) + 1 // i up to N
 * K[i] = Z[i] + 1 // i up to Q
 */
public class IntervalsKthLargest {
    /**
     * A continuous range of values where each value has the same number of duplicates.
     */
    static class FixedCountRange {
        public final int largestVal;
        public final int nLayers;
        public final int nLargerThan;
        FixedCountRange(int largestVal, int nLayers, int nLargerThan) {
            this.largestVal = largestVal;
            this.nLargerThan = nLargerThan;
            this.nLayers = nLayers;
        }
        static FixedCountRange keyForNumLargerVals(int numLargerValues) {
            return new FixedCountRange(-1, -1, numLargerValues);
        }
    }

    // Disjoint intervals where each interval has same number of duplicates for all its values
    // Ordered largest to smallest in largestVal
    // Ordered smallest to largest in nLargerThan
    private List<FixedCountRange> disjointIntervals;
    public final int numVals; // total count of values across all intervals

    int[] X;
    int[] Y;

    // O(NlogN + N) time
    IntervalsKthLargest(int N, int[] X, int[] Y) {
        this.X = X;
        this.Y = Y;
        List<Counter.Entry<Integer>> layerChLocsReversed = getLayerChangeLocsReverseOrder(N, X, Y);
        disjointIntervals = generateIntervals(layerChLocsReversed);
        if (disjointIntervals.size() > 0) {
            FixedCountRange lastNonEmptyInterval = disjointIntervals.get(disjointIntervals.size() - 1);
            int smallestValue = layerChLocsReversed.get(layerChLocsReversed.size() - 1).item + 1;
            numVals = lastNonEmptyInterval.nLargerThan
                    + lastNonEmptyInterval.nLayers * (lastNonEmptyInterval.largestVal - smallestValue + 1);
        } else {
            numVals = 0;
        }
    }

    // O(N) time
    private List<FixedCountRange> generateIntervals(List<Counter.Entry<Integer>> layerChangeLocsReverseOrder) {
        List<FixedCountRange> intervals = new ArrayList<>();
        FixedCountRange prevInterval = null;
        for (Counter.Entry<Integer> entry : layerChangeLocsReverseOrder) {
            int largestVal = entry.item;
            int nLayers = entry.count;
            if (prevInterval != null) {
                nLayers += prevInterval.nLayers;
            }
            int nLargerThan = 0;
            if (prevInterval != null) {
                nLargerThan = prevInterval.nLargerThan + prevInterval.nLayers * (prevInterval.largestVal - largestVal);
            }
            FixedCountRange newInterval = new FixedCountRange(largestVal, nLayers, nLargerThan);
            prevInterval = newInterval;
            // ignore intervals without any values so there will be no nLargerThan duplicates in the list
            if (nLayers > 0) { intervals.add(newInterval); }
        }
        return intervals;
    }

    // O(NlogN + N) time
    private List<Counter.Entry<Integer>> getLayerChangeLocsReverseOrder(int N, int[] X, int[] Y) {
        // retrieve all values where there's a change in layers, along with accumulated change value
        // set counter to drop 0-counts so only values which have a change in layers are tracked
        Counter<Integer> layerChanges = new Counter<>(true, false);
        for (int i = 0; i < N; i++) { // process each range and accumulate layer changes
            int start = X[i] + 1;
            int end = Y[i] + 1;
            if (start > end) { // make sure start <= end by swapping if needed
                start ^= end;
                end ^= start;
                start ^= end;
            }
            layerChanges.incrementCountFor(end); // increase in layers at end value when traversing largest to smallest
            layerChanges.decrementCountFor(start - 1); // drop in layers from the next value < start
        }

        List<Counter.Entry<Integer>> layerChangeLocs = layerChanges.getEntries();
        layerChangeLocs.sort(Comparator
                .comparingInt((Counter.Entry<Integer> x) -> x.item)
                .reversed()); // sort by values largest to smallest
        return layerChangeLocs;
    }

    // O(logN) time
    int getKthLargestValue(int k) {
        if (k > numVals) { return 0; }
        // binary search to find the index of the fixed count range containing the kth largest value
        // disjointIntervals is already sorted ascending on number of values larger than it

        int i = Collections.binarySearch(disjointIntervals,
                FixedCountRange.keyForNumLargerVals(k - 1), // kth largest val has (k-1) values larger than it
                Comparator.comparingInt(x -> x.nLargerThan));

        if (i < 0) { // kth largest value is not the largest value in any interval
            i = -(i + 1); // insertion point index for (k-1) in countLarger array
            // insertion point is also the first interval that does NOT contain the kth largest
            i--; // will never be less than 0 because k is positive
        }
        // kth largest value is the rth largest value in this interval
        // guaranteed to fall inside interval
        FixedCountRange containingRange = disjointIntervals.get(i);
        int r = k - containingRange.nLargerThan;
        int kth = containingRange.largestVal - (r - 1) / containingRange.nLayers;
        if (kth < 1) {
            throw new IllegalStateException();
        }
        return kth;
    }

    long genAnswerProof(int[] Z) {
        long sum = 0;
        for (int i = 0; i < Z.length; i++) {
            sum += ((long) i + 1) * getKthLargestValue(Z[i] + 1);
        }
        return sum;
    }

    static int[] genSequence(int len, int v1, int v2, int a, int b, int c, int m) {
        int[] seq = new int[len];
        if (len > 0) seq[0] = v1;
        if (len > 1) seq[1] = v2;
        for (int i = 2; i < len; i++) {
            long val = (Math.addExact(
                    Math.multiplyExact((long) a, (long) seq[i-1]),
                    Math.multiplyExact((long) b, (long) seq[i-2]))
                    + c) % m; // prevent overflow
            seq[i] = Math.toIntExact(val); // mod M ensures final fit in 32 bits
            if (seq[i] < 0) {
                int b2 = seq[i-2];
                int b1 = seq[i-1];
                System.out.println("long: " + val + "int: " + seq[i]);
                throw new IllegalStateException();
            }
        }
        return seq;
    }

    // todo failed for small and large test cases
    public static void main(String... args) throws Exception {
        Path inPath = ProblemSetIO.askForInputFile();
        Path outPath = Paths.get(inPath.toString()+ ".out");

        ProblemSetIO.googleCodeJam(inPath, outPath, (t, in, out) -> {
            int N = in.nextInt();
            int Q = in.nextInt();
            int[] X = genSequence(N, in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
            int[] Y = genSequence(N, in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
            int[] Z = genSequence(Q, in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
            IntervalsKthLargest solver = new IntervalsKthLargest(N, X, Y);
            out.println("Case #" + t + ": " + solver.genAnswerProof(Z));
        });
    }
}
