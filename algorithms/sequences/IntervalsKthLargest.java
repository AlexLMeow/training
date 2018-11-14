package sequences;

import util.*;

import java.nio.file.Path;
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
        final long largestVal;
        final int nLayers;
        final long nLargerThan;
        FixedCountRange(long largestVal, int nLayers, long nLargerThan) {
            this.largestVal = largestVal;
            this.nLargerThan = nLargerThan;
            this.nLayers = nLayers;
        }
        static FixedCountRange keyForNumLargerVals(long numLargerValues) {
            return new FixedCountRange(-1, -1, numLargerValues);
        }
    }

    // Disjoint intervals where each interval has same number of duplicates for all its values
    // Ordered largest to smallest in largestVal
    // Ordered smallest to largest in nLargerThan
    private List<FixedCountRange> disjointIntervals;
    public final long numVals; // total count of values across all intervals

    long[] X;
    long[] Y;

    // O(NlogN + N) time
    IntervalsKthLargest(int N, long[] X, long[] Y) {
        this.X = X;
        this.Y = Y;
        List<Counter.Entry<Long>> layerChLocsReversed = getLayerChangeLocsReverseOrder(N, X, Y);
        disjointIntervals = generateIntervals(layerChLocsReversed);
        if (disjointIntervals.size() > 0) {
            FixedCountRange lastInterval = disjointIntervals.get(disjointIntervals.size() - 1);
            long smallestValue = layerChLocsReversed.get(layerChLocsReversed.size() - 1).item + 1;
            long numValsInLastInterval = lastInterval.nLayers * (lastInterval.largestVal - smallestValue + 1);
            numVals = lastInterval.nLargerThan + numValsInLastInterval;
        } else {
            numVals = 0;
        }
    }

    // O(N) time
    private List<FixedCountRange> generateIntervals(List<Counter.Entry<Long>> layerChangeLocsReverseOrder) {
        List<FixedCountRange> intervals = new ArrayList<>();
        FixedCountRange prevInterval = new FixedCountRange(Long.MAX_VALUE, 0, 0);
        for (Counter.Entry<Long> entry : layerChangeLocsReverseOrder) {
            long largestVal = entry.item;
            int layerDelta = entry.count;
            int nLayers = prevInterval.nLayers + layerDelta;
            long nLargerThan = prevInterval.nLargerThan + prevInterval.nLayers * (prevInterval.largestVal - largestVal);
            
            FixedCountRange newInterval = new FixedCountRange(largestVal, nLayers, nLargerThan);
            // ignore intervals without any values so there will be no nLargerThan duplicates in the list
            if (nLayers > 0) { intervals.add(newInterval); }
            prevInterval = newInterval;
        }
        return intervals;
    }

    // O(NlogN + N) time
    private List<Counter.Entry<Long>> getLayerChangeLocsReverseOrder(int N, long[] X, long[] Y) {
        // retrieve all values where there's a change in layers, along with accumulated change value
        // set counter to drop 0-counts so only values which have a change in layers are tracked
        Counter<Long> layerChanges = new Counter<>(true, false);
        for (int i = 0; i < N; i++) { // process each range and accumulate layer changes
            long lowerBound = X[i] + 1;
            long upperBound = Y[i] + 1;
            if (lowerBound > upperBound) { // enforce lower <= upper
                lowerBound ^= upperBound;
                upperBound ^= lowerBound;
                lowerBound ^= upperBound;
            }
            layerChanges.incrementCountFor(upperBound); // when traversing largest to smallest, ranges 'start' at their upper bound
            layerChanges.decrementCountFor(lowerBound - 1); // and ranges 'end' at the next integer 'after' their lower bound
        }

        List<Counter.Entry<Long>> layerChangeLocs = layerChanges.getEntries();
        layerChangeLocs.sort(Comparator
                .comparingLong((Counter.Entry<Long> x) -> x.item) // it's 2018 and java type generics are still a clusterfuck
                .reversed()); // sort by values largest to smallest
        return layerChangeLocs;
    }


    long getKthLargestValue(long K) {
        if (K < 1) { throw new IllegalArgumentException(); }
        if (K > numVals) { return 0; }
        return getValueWithKLargerScores(K - 1);
    }

    // O(logN) time
    long getValueWithKLargerScores(long k) {
        if (k >= numVals) { return 0; }
        // binary search to find the index of the fixed count range containing value with k larger values than it
        // disjointIntervals is already sorted ascending on number of values larger than it

        int i = Collections.binarySearch(disjointIntervals,
                FixedCountRange.keyForNumLargerVals(k),
                Comparator.comparingLong(x -> x.nLargerThan));

        if (i < 0) { // target value is not the largest value in any interval
            i = -(i + 1); // insertion point index (first interval that has all values smaller than target)
            i--; // so interval containing target value is the previous one
        }
        
        FixedCountRange containingRange = disjointIntervals.get(i);
        long r = k - containingRange.nLargerThan; // target value has r values larger than it WITHIN THE INTERVAL
        long target = containingRange.largestVal - (r / containingRange.nLayers);
        return target;
    }

    long genAnswerProof(long[] Z) {
        long sum = 0;
        for (int i = 0; i < Z.length; i++) {
            sum += getKthLargestValue(Z[i] + 1) * (i + 1);
        }
        return sum;
    }

    static long[] genSequence(int len, long v1, long v2, long a, long b, long c, long m) {
        long[] seq = new long[len];
        if (len > 0) seq[0] = v1;
        if (len > 1) seq[1] = v2;
        for (int i = 2; i < len; i++) {
            seq[i] = (a * seq[i-1] + b * seq[i-2] + c) % m;
        }
        return seq;
    }

    public static void main(String... args) throws Exception {
        Path inPath = ProblemSetIO.askForInputFile();
        Path outPath = ProblemSetIO.defaultOutFile(inPath);

        ProblemSetIO.googleCodeJamFileIO(inPath, outPath, (t, in, out) -> {
            int N = in.nextInt();
            int Q = in.nextInt();
            long[] X = genSequence(N, in.nextLong(), in.nextLong(), in.nextLong(), in.nextLong(), in.nextLong(), in.nextLong());
            long[] Y = genSequence(N, in.nextLong(), in.nextLong(), in.nextLong(), in.nextLong(), in.nextLong(), in.nextLong());
            long[] Z = genSequence(Q, in.nextLong(), in.nextLong(), in.nextLong(), in.nextLong(), in.nextLong(), in.nextLong());
            IntervalsKthLargest solver = new IntervalsKthLargest(N, X, Y);
            out.println("Case #" + t + ": " + solver.genAnswerProof(Z));
        });
    }
}
