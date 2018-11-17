package sets_and_tuples;

import util.Counter;
import util.MathUtil;
import util.ProblemSetIO;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.*;

public class ProductTriplets {

	/**
	 * Google Code Jam Kickstart Round G 2018
	 * <p>
	 * Given N integers, count the number of triplets {x,y,z} such that:
	 * At least one of the following is true:
	 * x = y * z
	 * y = x * z
	 * z = x * y
	 * degenerates to: find all pairs (x,y) that multiply to form a third number z. (we can swap x,y,z)
	 * <p>
	 */

	static long solve(long[] values) {
		Counter<Long> counter = new Counter<>(Arrays.stream(values).boxed().collect(Collectors.toList()));
		long[] uniques = counter.getItemSet().stream().mapToLong(x -> x).toArray();
		long totalCount = 0;
		for (int i = 0; i < uniques.length; i++) {
			long x = uniques[i];
			for (int j = i; j < uniques.length; j++) {
				long y = uniques[j];
				long z = x * y;
				// ways to choose values matching (x,y,z) is the product of choosing [count in x,y,z] values
				// from [count in full list].
				long choices = 1;
				for (Counter.Entry<Long> e : new Counter<>(x, y, z).getEntries()) {
					choices *= MathUtil.nCk(counter.getCountFor(e.item), e.count);
				}
				totalCount += choices;
			}
		}
		return totalCount;
	}

	static long solveAllDistinct(long[] values) {
		Set<Long> valSet = new HashSet<>(values.length);
		for (long v : values) {
			valSet.add(v);
		}

		int count = 0;
		for (int i = 0; i < values.length; i++) {
			long x = values[i];
			for (int j = i + 1; j < values.length; j++) {
				long y = values[j];
				long z = x * y;
				if (z != x && z != y && valSet.contains(z)) {
					count++;
				}
			}
		}
		return count;
	}

	public static void main(String... args) throws IOException {
		Path inPath = ProblemSetIO.askForInputFile();
		Path outPath = Paths.get(inPath.toString() + ".out");

		ProblemSetIO.googleCodeJamFileIO(inPath, outPath, (t, in, out) -> {
			int N = in.nextInt();
			in.nextLine();
			long[] values = Arrays.stream(in.nextLine().split(" ")).mapToLong(Long::parseLong).toArray();
			long count = solve(values);
			out.println("Case #" + t + ": " + count);
		});
	}
}
