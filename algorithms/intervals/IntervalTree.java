package intervals;

import java.util.*;

public class IntervalTree {

	// interval trees support updates
	// remembers the actual intervals


	// Augment a self balancing tree with maxEnd. key is Interval, sorted by start then end

	static class Node {
		Interval interval;
		int maxEnd; // max end value contained in this subtree. May be left.maxEnd, right.maxEnd, or interval.end
		Node left; // every interval in left subtree has smaller start or same start and smaller end
		Node right; // every interval in right subtree has bigger start or same start and bigger end
	}

	/**
	 * O(n) time because every interval in tree might contain point.
	 * We can do a few short circuits to improve average runtime
	 */
	static List<Interval> findAllContaining(Node root, int point) {
		return findAllOverlapping(root, new Interval(point, point));
	}
	static List<Interval> findAllOverlapping(Node root, Interval target) {
		// target is larger than everything in tree
		if (root == null || target.start > root.maxEnd) {
			return new ArrayList<>();
		}
		// all we know about left subtree is that intervals there have ste starts, and nothing about the ends
		// this means it's always possible for left subtree to contain a fulfilling interval
		List<Interval> overlapping = findAllOverlapping(root.left, target);
		// add this node's interval if necessary
		if (root.interval.overlaps(target)) {
			overlapping.add(root.interval);
		}
		// only search right subtree if it is possible for target to overlap with intervals with a gte start
		if (target.end >= root.interval.start) {
			overlapping.addAll(findAllOverlapping(root.right, target));
		}
		return overlapping;
	}

	/**
	 * O(logn) time
	 */
	static Interval findAnyContaining(Node root, int point) {
		return findAnyOverlapping(root, new Interval(point, point));
	}
	static Interval findAnyOverlapping(Node root, Interval target) {
		if (root == null || target.start > root.maxEnd) return null;
		if (root.interval.overlaps(target)) return root.interval;
		Interval candidate = findAnyOverlapping(root.left, target); // either O(1) or O(log(n)), see below for explanation
		if (candidate != null) return candidate; // O(log(n)) time
		// at this point target is to the right of any intervals in left subtree
		// which means it was immediately detected by the first line check, making the time elapsed so far O(1)
		if (target.end >= root.interval.start) return findAnyOverlapping(root.right, target); // O(log(n))
		return null; // not found
	}

	// todo construct, add, remove
}
