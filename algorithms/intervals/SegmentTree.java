package intervals;

import util.MathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SegmentTree<E> {

	// used when the domain is discrete and bounded (eg. array of length N)
	// does not support structure update after construction (so can represent it as an array)
	// STRUCTURE: each node represents the union of the segments represented by its children
	// leaves are individual elements (length 1 segments)
	// tree is balanced by construction, and every node either has 0 or 2 children, never 1.

	// supports updating elements in a segment
	// supports querying a segment for an the result of an associative operation (eg. min, max, sum)

	static class Node<E> {
		E val;
		Node<E> left;
		Node<E> right;
		Node() {}
		Node(E val) { this.val = val; }
	}

	private Node<E> root;
	public final int domainSize;
	private BinaryOperator<E> op;
	private E id; // id value for the op (eg. 0 for sum, +infinity for min)

	SegmentTree(List<E> base, BinaryOperator<E> op, E id) {
		if (base.size() == 0) throw new IllegalArgumentException();
		this.domainSize = base.size();
		this.op = op;
		this.id = id;
		this.root = constructTree(0, domainSize - 1, base);
	}

	// O(n)
	private Node<E> constructTree(int start, int end, List<E> base) {
		if (start == end) { // leaf
			return new Node<>(base.get(start));
		}
		Node<E> root = new Node<>();
		root.left = constructTree(start, leftEnd(start, end), base);
		root.right = constructTree(rightStart(start, end), end, base);
		root.val = op.apply(root.left.val, root.right.val);
		return root;
	}


	// log(n) see explanation below
	E query(int start, int end) {
		if (start > end || start < 0 || end >= domainSize) throw new IndexOutOfBoundsException();
		return query(root, 0, domainSize-1, start, end);
	}
	private E query(Node<E> root, int rootStart, int rootEnd, int targetStart, int targetEnd) {
		// if target and root intervals don't intersect, return id value
		if (targetStart > rootEnd || targetEnd < rootStart) return id;

		// if target interval contains root interval, return root's value
		if (targetStart <= rootStart && targetEnd >= rootEnd) return root.val;

		// else they overlap but there is some segment in root that lies outside target
		return op.apply(
						query(root.left, rootStart, leftEnd(rootStart, rootEnd), targetStart, targetEnd),
						query(root.right, rightStart(rootStart, rootEnd), rootEnd, targetStart, targetEnd)
		);
		// O(log(n)) because log(n) to find start and end leafs, and combines with each sibling along path,
		// and those siblings return O(1) because they will fulfill above conditions
	}

	// log(n)
	void update(int index, UnaryOperator<E> transformer) {
		if (index < 0 || index >= domainSize) throw new IndexOutOfBoundsException();
		update(root, 0, domainSize-1, index, transformer);
	}
	private E update(Node<E> root, int rootStart, int rootEnd, int target, UnaryOperator<E> transform) {
		// found leaf, apply direct change
		if (rootStart == rootEnd) {
			root.val = transform.apply(root.val);
			return root.val;
		}

		// leaf is in one of children, get result before recombining to get new root val
		if (target <= leftEnd(rootStart, rootEnd)) { // target in left child
			root.val = op.apply(
							update(root.left, rootStart, leftEnd(rootStart, rootEnd), target, transform),
							root.right.val);

		// target in right child
		} else {
			root.val = op.apply(
							root.left.val,
							update(root.right, rightStart(rootStart, rootEnd), rootEnd, target, transform));
		}
		return root.val;
	}

	static int leftEnd(int start, int end) {
		return start + (end - start)/2;
	}
	static int rightStart(int start, int end) {
		return leftEnd(start, end) + 1;
	}

	public static void main(String... args) {

		new SegmentTree<Integer>(IntStream.of(0,9,5,7,3).boxed().collect(Collectors.toList()), Math::addExact, 0);
	}
}
