package sets_and_tuples;
import util.ProblemSetIO;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.*;

public class ProductTriplets {

    /** Google Code Jam Kickstart Round G 2018
     *
     * Given N integers, count the number of triplets {x,y,z} such that:
     * At least one of the following is true:
     * x = y * z
     * y = x * z
     * z = x * y
     * degenerates to: find all pairs (x,y) that multiply to form a third number z. (we can swap x,y,z)
     *
     *
     * GENERALIZATION: K variables in equation (eg. a + b + c = d + e + f)
     * - Half in loops, other half (d+e+f) stored in hashmap
     * - hashmap key = result of operation, value = list of K/2-tuples
     * - also store counter of individual values
     * eg. 6 vars
     * for (a in vals):
     *    for (b in vals):
     *        for (c in vals):
     *            combined = a + b + c
     *            matching_triplets = triplemap.get(combined) // unique triplets that also sum to form a+b+c
     *            for (d,e,f in matching_triplets):
     *                instances = new Counter(a,b,c,d,e,f)
     *                for (x,cnt in instances):
     *                    totalSum += nCk(freqs.countOf(x), cnt) // ways to create this 6-tuple from full list including dups
     */

    static long solve(long[] values) {
        ValCounter counter = new ValCounter(values);
        long[] uniques = counter.getUniqueVals();
        long count = 0;
        for (int i = 0; i < uniques.length; i++) {
            long x = uniques[i];
            for (int j = i; j < uniques.length; j++) {
                long y = uniques[j];
                long z = x * y;
                // ways to choose values matching (x,y,z) is the product of choosing [count in x,y,z] values
                // from [count in full list].
                long choices = 1;
                for (ValCounter.ValCount c : new ValCounter(x, y, z).getAllCounts()) {
                    choices *= nCk(counter.countOf(c.val), c.count);
                }
                count += choices;
            }
        }
        return count;
    }

    static long solveAllDistinct(long[] values) {
        Set<Long> valSet = new HashSet<>(values.length);
        for (long v : values) { valSet.add(v); }

        int count = 0;
        for (int i = 0; i < values.length; i++) {
            long x = values[i];
            for (int j = i+1; j < values.length; j++) {
                long y = values[j];
                long z = x * y;
                if (z != x && z != y && valSet.contains(z)) {
                    count++;
                }
            }
        }
        return count;
    }

    static long nCk(int n, int k) {
        long res = 1; // nC(0) is 1
        for (int kk = 1; kk <= k; kk++) { // MUST BUILD UP FROM K=0, SO THAT EVERY STEP IS AN INTEGER
            double real = ((double) res) * (n-kk+1) / kk;
            res = Math.round(real);
        }
        return res;
    }

    // C:\Users\yijin\Desktop\Dropbox (Personal)\admin\resoom\SE INTERVIEW\test-cases\product-triplets-small.in
    // C:\Users\yijin\Desktop\Dropbox (Personal)\admin\resoom\SE INTERVIEW\test-cases\product-triplets-large.in
    // C:\Users\yijin\Desktop\Dropbox (Personal)\admin\resoom\SE INTERVIEW\test-cases\a.txt
    public static void main(String... args) throws IOException {

        Path inPath = ProblemSetIO.askForInputFile();
        Path outPath = Paths.get(inPath.toString()+ ".out");

        ProblemSetIO.googleCodeJam(inPath, outPath, (t, in, out) -> {
            int N = in.nextInt();
            in.nextLine();
            long[] values = Arrays.stream(in.nextLine().split(" ")).mapToLong(Long::parseLong).toArray();
            long count = solve(values);
            out.println("Case #" + (t+1) + ": " + count);
        });
    }

    static class ValCounter {
        static class ValCount {
            final long val;
            final int count;
            ValCount(long val, int count) {
                this.val = val;
                this.count = count;
            }
        }
        private Map<Long, Integer> counts;
        ValCounter(long... vals) {
            counts = new HashMap<>();
            for (long v : vals) {
                Integer count = counts.get(v);
                if (count == null) {
                    count = 1;
                } else {
                    count++;
                }
                counts.put(v, count);
            }
        }
        public int countOf(long val) {
            Integer count = counts.get(val);
            return count == null ? 0 : count;
        }
        public long[] getUniqueVals() {
            return counts.keySet().stream()
                    .mapToLong(x -> x)
                    .toArray();
        }
        public List<ValCount> getAllCounts() {
            return counts.entrySet().stream()
                    .map(e -> new ValCount(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
        }
    }
}
