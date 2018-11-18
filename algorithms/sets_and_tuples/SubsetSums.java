package sets_and_tuples;

import java.util.*;
import java.util.stream.Collectors;

public class SubsetSums {


	// 0 -4 -1 -4 -2 -3 2
	public List<List<Integer>> threeSum(int[] nums) {
		Arrays.sort(nums);
		List<List<Integer>> trips = new ArrayList<>();

		for (int i = 0; i < nums.length - 2; i++) { // after every step, we have found the triplets with lowest elem = A[i] // but what if is duplicate of previous a? then move to the next non duplicate
			while (i > 0 && nums[i] == nums[i-1] && ++i < nums.length - 2);
			// with this, we will only have processed the first instance of each a


			int a = nums[i];
			// need to find b c pair on right that satisfies b+c=-a
			int l = i + 1; // left bound
			int r = nums.length - 1; // right bound
			while (l < r) { // find all (b,c) pairs by contracting window
				int b = nums[l];
				int c = nums[r]; // now a <= b <= c
				if (b + c > -a) {
					r--; // sum too big, make smaller by stepping down higher bound
				} else if (b + c < -a) {
					l++; // sum too small, make bigger by stepping up lower bound

				} else { // matches
					trips.add(Arrays.asList(a, b, c)); // add triplet first time it is seen
					// but what if duplicates?
					// then contract bounds till not duplicate
					while (nums[++l] == b && l < r);
					while (nums[--r] == c && l < r);
				}
			}
		}

		return trips;
	}

	// all unique quadtuples where a+b+c+d = k
	public List<List<Integer>> fourSum(int[] nums, int K) {
		Map<Integer, SortedSet<Integer>> pairSums = new HashMap<>(); // sum -> [involved elems] (can generate other elem in pair by sum - X)
		Map<Integer, Integer> elemFreqs = new HashMap<>();
		Set<List<Integer>> tuples = new HashSet<>();

		for (int i = 0; i < nums.length; i++) {
			int x = nums[i];
			elemFreqs.merge(x, 1, Math::addExact);
			for (int j = i + 1; j < nums.length; j++) {
				int y = nums[j];
				int key = x + y;
				pairSums.compute(key, (s, factors) -> {
					if (factors == null) factors = new TreeSet<>();
					factors.add(x < y ? x : y); // only put smaller of pair, since u can retrieve other in pair by subtraction from key
					return factors;
				});
			}
		}
		Arrays.sort(nums);

		// for every distinct pair:
		// we can find all pairs that fulfill the condition a+b+c+d=K
		// but need handle not enough elems to fill the pairs eg. (a,b)(b,c) but only one b)
		// and repetition eg. (a,b)(b,c) -> (a,c)(b,b)
		for (int i = 0; i < nums.length; i++) {
			while (i > 0 && nums[i] == nums[i-1] && ++i < nums.length); // skip dups
			if (i >= nums.length) break; // handle last string of dupes
			int a = nums[i];
			for (int j = i + 1; j < nums.length; j++) {
				while (j > i + 1 && nums[j] == nums[j-1] && ++j < nums.length); // skip dups
				if (j >= nums.length) break; // handle last string of dupes
				int b = nums[j];
				int cdSum = K - a - b;
				SortedSet<Integer> cs = pairSums.get(cdSum);
				if (cs == null || cs.isEmpty()) continue; // not possible to build a quad with this a and b
				// todo HANDLE DUPES
				for (int c : cs) { // smallest c onwards
					int d = cdSum - c;
					if (isPossible(elemFreqs, a, b, c, d)) {
						List<Integer> tuple = new ArrayList<>();
						tuple.add(a);
						tuple.add(b);
						tuple.add(c);
						tuple.add(d);
						tuple.sort(null);
						tuples.add(tuple);
					}
				}
			}
		}

		return new ArrayList<>(tuples);
	}

	static boolean isPossible(Map<Integer, Integer> freqs, int... values) {
		Map<Integer, Integer> counts = new HashMap<>();
		for (int v : values) {
			counts.merge(v, 1, Math::addExact);
		}
		for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
			if (freqs.getOrDefault(entry.getKey(), 0) < entry.getValue())
				return false;
		}
		return true;
	}


	public static void main(String... args) {
//		System.out.println(new SubsetSums().threeSum(
//						Arrays.stream(new Scanner(System.in).nextLine().split(",")).mapToInt(Integer::parseInt).toArray()));
//		System.out.println(reorganizeString("aab"));
	}
}
