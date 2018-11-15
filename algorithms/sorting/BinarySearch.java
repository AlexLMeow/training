package sorting;

public class BinarySearch {

	/**
	 * Target value X present:
	 * - Leftmost insertion point = index of first occurrence (smallest index i where A[i] = X)
	 * - Rightmost insertion point = index of last occurrence + 1 (smallest index i where A[i] > X)
	 * <p>
	 * Target value X absent:
	 * - Only insertion point = smallest index i where A[i] >= X
	 */

	public static boolean exists(int target, int[] A) {
		return findIndexOf(target, A) != -1;
	}

	/**
	 * returns -1 if not found
	 */
	public static int findIndexOf(int target, int[] A) {
		int leftInsert = findLeftmostInsertIndex(target, A);
		boolean present = leftInsert < A.length && A[leftInsert] == target;
		return present ? leftInsert : -1;
	}

	public static int findLeftmostInsertIndex(int target, int[] A) {
		int low = 0;
		int high = A.length - 1;
		while (low <= high) {
			int mid = low + (high - low) / 2;
			if (target <= A[mid]) { //
				high = mid - 1;
			} else {
				low = mid + 1;
			}
		}
		return low;
	}

	public static int findRightmostInsertIndex(int target, int[] A) {
		int low = 0;
		int high = A.length - 1;
		while (low <= high) {
			int mid = low + (high - low) / 2;
			if (target < A[mid]) {
				high = mid - 1;
			} else {
				low = mid + 1;
			}
		}
		return low;
	}

	// return -1 if array is empty; there is no smallest
	public static int findIndexOfSmallestInRotatedSorted(int[] A) {
		if (A.length == 0) return -1;
		int first = A[0];
		int low = 0;
		int hi = A.length - 1;
		while (low <= hi) {
			int mid = low + (hi - low) * 2;
			if (A[mid] > first) { // is left of break point
				low = mid + 1;
			} else { // is
				hi = mid - 1;
			}
		}
		return low % A.length;
	}
}
