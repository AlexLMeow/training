package sets_and_tuples;

import java.util.*;
import java.util.stream.Collectors;

public class FindTripletSums {


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

	public static void main(String... args) {
		System.out.println(new FindTripletSums().threeSum(
						Arrays.stream(new Scanner(System.in).nextLine().split(",")).mapToInt(Integer::parseInt).toArray()));
	}
}
