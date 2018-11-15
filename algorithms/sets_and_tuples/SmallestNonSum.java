package sets_and_tuples;

import java.util.*;

public class SmallestNonSum {
	/**
	 * Given a list of N positive integers, find the smallest positive integer that CANNOT be formed by a subset sum
	 */

	/**
	 * let T(i) be smallest +ve int that cannot be formed by a subset sum of the {i} smallest values
	 * let val(i) be the i-th smallest value (order statistic)
	 * <p>
	 * T(0) = 1 // empty set
	 * <p>
	 * T(i) = T(i-1) + val(i)     :: if { val(i) <= T(i-1) }
	 * = T(i-1)              :: if { val(i) > T(i-1) }
	 * <p>
	 * ## Explanation:
	 * <p>
	 * Let the set of the i smallest values be S(i).
	 * Let the i-th smallest value be V(i).
	 * <p>
	 * The subset sums of S(i-1) form a continuous block on the int number line: from 0 to T(i-1)-1.
	 * There may be more blocks starting after T(i-1). They are not relevant for calculating the smallest non-sum.
	 * <p>
	 * To get T(i) you have to consider S(i) which now includes new subsets formed that contain V(i).
	 * Including V(i) in a set of positive numbers increases the sum by V(i). We do this for every subset in S(i-1).
	 * This creates new subset sums represented by right shifting by V(i) the subset sum blocks for S(i-1).
	 * <p>
	 * ## Visualization:
	 * v -------- old max / T(i-1)
	 * |||||||||        -- subset sums for S(i-1)
	 * |||||||||    -- subset sums with V(i) added
	 * <p>
	 * ## UNION ##
	 * v ---- new max / T(i)
	 * |||||||||||||    MERGED
	 * <p>
	 * The union of the 2 groups of subset sum blocks represent the subset sum values of S(i).
	 * If V(i) <= T(i-1), the first blocks will merge as one continuous block
	 * else V(i) > T(i-1), there will be a gap starting at T(i-1).
	 * <p>
	 * ## Correctness:
	 * There will only be one continuous subset sum block until the first gap is found by definition
	 * The gap is caused by a value bigger than the first block itself
	 * All future even larger values will never close the first gap since the leftmost block (starting from 0)
	 * will always be right shifted beyond the end of the first block (where the first gap is)
	 */
	public int solveIterative(List<Integer> values) {
		List<Integer> sorted = new ArrayList<>(values);
		Collections.sort(sorted);
		int candidate = 1; // init for T(0)
		for (int v : sorted) {
			if (v > candidate) {
				return candidate;
			}
			candidate += v;
		}
		return candidate;
	}

	public int solveRecursive(List<Integer> values) {
		List<Integer> sorted = new ArrayList<>(values);
		Collections.sort(sorted);
		return T(sorted.size(), sorted);
	}

	/**
	 * T is the smallest non-sum when considering only the i smallest values
	 * See giant block comment above for explanation
	 */
	private int T(int i, List<Integer> sorted) {
		if (i == 0) {
			return 1;
		}
		int index = i - 1;
		int val = sorted.get(index);
		int prev = T(i - 1, sorted);
		if (val > prev) {
			return prev;
		} else {
			return val + prev;
		}
	}


}
