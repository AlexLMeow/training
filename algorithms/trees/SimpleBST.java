package trees;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

/**
 * Simplest BST structure.
 * No augmentations, no balance guarantees.
 * O(n) time for all rank and indexing type operations:
 * - get(index), successor(), predecessor(), indexOf(item)
 */
public class SimpleBST<E extends Comparable<E>> implements BinaryTree<E> {

	protected static class Node<E extends Comparable<E>> {
		protected E val;
		protected Node<E> left;
		protected Node<E> right;
//		protected Node parent;

		protected Node(E val) {
			this(val, null, null);
		}
		protected Node(E val, Node left, Node right) {
			this.val = val;
			this.left = left;
			this.right = right;
		}

		protected int recursiveGetSize() {
			int size = 1;
			size += this.left == null ? 0 : this.left.recursiveGetSize();
			size += this.right == null ? 0 : this.right.recursiveGetSize();
			return size;
		}
		protected int recursiveGetHeight() {
			if (this.isLeaf()) { return 0; }
			int leftH = this.hasLeftChild() ? this.left.recursiveGetHeight() : Integer.MIN_VALUE;
			int rightH = this.hasRightChild() ? this.right.recursiveGetHeight() : Integer.MIN_VALUE;
			return 1 + Math.max(leftH, rightH);
		}

		protected boolean iterativeContains(E item) {
			return this.iterativeFindNode(item) != null;
		}
		protected boolean recursiveContains(E item) {
			return this.recursiveFindNode(item) != null;
		}

		protected Node<E> recursiveFindSmallest() {
			if (!this.hasLeftChild()) { return this; }
			return this.left.recursiveFindSmallest();
		}
		protected Node<E> iterativeFindSmallest() {
			Node<E> current = this;
			while (current.hasLeftChild()) {
				current = current.left;
			}
			return current;
		}
		protected Node<E> recursiveFindLargest() {
			if (!this.hasRightChild()) { return this; }
			return this.right.recursiveFindSmallest();
		}
		protected Node<E> iterativeFindLargest() {
			Node<E> current = this;
			while (current.hasRightChild()) {
				current = current.right;
			}
			return current;
		}

		protected Node<E> recursiveFindNode(E item) {
			if (item.equals(this.val)) { return this; }
			if (item.compareTo(this.val) < 0) { // item < this
				return this.hasLeftChild() ? this.left.recursiveFindNode(item) : null;
			} else { // item > this
				return this.hasRightChild() ? this.right.recursiveFindNode(item) : null;
			}
		}
		protected Node<E> iterativeFindNode(E item) {
			Node<E> current = this;
			while (current != null) {
				if (item.equals(current.val)) { return current; }
				current = item.compareTo(current.val) < 0 ? current.left : current.right;
			}
			return null;
		}

		/**
		 * No duplicates allowed. Does not re-balance tree.
		 * @return true if success, false if duplicate
		 */
		protected boolean recursiveAdd(E item) {
			// detect duplicates
			if (item.equals(this.val)) { return false; }
			// item < this
			if (item.compareTo(this.val) < 0) {
				if (this.hasLeftChild()) { return this.left.recursiveAdd(item); }
				// space to add
				this.left = new Node<>(item);
				return true;

			// item > this
			} else {
				if (this.hasRightChild()) { return this.right.recursiveAdd(item); }
				// space to add
				this.right = new Node<>(item);
				return true;
			}
		}
		/**
		 * No duplicates allowed. Does not re-balance tree.
		 * @return true if success, false if duplicate
		 */
		protected boolean iterativeAdd(E item) {
			Node<E> current = this;
			while (current != null) {
				// detect duplicates
				if (current.val.equals(item)) { return false; }

				// item < current
				if (item.compareTo(current.val) < 0) {
					if (current.hasLeftChild()) {
						current = current.left;
					} else { // insert point
						current.left = new Node<>(item);
						break;
					}

				// item > current
				} else {
					if (current.hasRightChild()) {
						current = current.right;
					} else { // insert point
						current.right = new Node<>(item);
						break;
					}
				}
			}
			return true;
		}

		/**
		 * Will never increase tree height.
		 * @return new root of this subtree, will be null if this node has no children. All pointers are updated properly.
		 */
		protected Node<E> removeSelf() {
			// 2 children, replace with successor in descendants
			if (this.hasLeftChild() && this.hasRightChild()) {
				Node<E> descSuccessor = this.right;
				Node<E> parent = this;
				while (descSuccessor.hasLeftChild()) { // successor is smallest element in right subtree
					parent = descSuccessor; // since parent might not be getting removed, will need to update parent pointers
					descSuccessor = descSuccessor.left;
				}
				if (parent != this) {
					parent.left = descSuccessor.removeSelf();
					descSuccessor.right = this.right; // inherits self's right child
				}
				descSuccessor.left = this.left; // now all pointers are updated.
				return descSuccessor;

			// self only has left child
			} else if (this.hasLeftChild()) {
				return this.left;

			// self only has right child
			} else if (this.hasRightChild()) {
				return this.right;

			// no children :(
			} else {
				return null;
			}
		}

		protected Node<E> successor() {
			return this.right == null ? null : this.right.iterativeFindSmallest();
		}
		protected Node<E> predecessor() {
			return this.left == null ? null : this.left.iterativeFindLargest();
		}


		protected boolean hasLeftChild() { return this.left != null; }
		protected boolean hasRightChild() { return this.right != null; }
		protected boolean isLeaf() { return !hasLeftChild() && !hasRightChild(); }

		protected void iterativeTraversePreOrder(Consumer<Node<E>> itemHandler) {
			Deque<Node<E>> notVisited = new ArrayDeque<>();
			notVisited.push(this); // start with root of subtree
			while (notVisited.size() > 0) {
				Node<E> current = notVisited.pop();
				itemHandler.accept(current); // pre-order = act when first visited
				if (current.hasRightChild()) { notVisited.push(current.right); }
				if (current.hasLeftChild()) { notVisited.push(current.left); }
			}
		}
		protected void recursiveTraversePreOrder(Consumer<Node<E>> itemHandler) {
			itemHandler.accept(this);
			if (this.left != null) { this.left.recursiveTraversePreOrder(itemHandler); }
			if (this.right != null) { this.right.recursiveTraversePreOrder(itemHandler); }
		}
		protected void iterativeTraverseInOrder(Consumer<Node<E>> itemHandler) {
			Deque<Node<E>> notVisitedLeft = new ArrayDeque<>();
			Deque<Node<E>> visitedLeft = new ArrayDeque<>();
			notVisitedLeft.push(this); // start with root of subtree
			boolean isNextVisitedValid = false;
			while (!notVisitedLeft.isEmpty() || !visitedLeft.isEmpty()) {
				if (isNextVisitedValid) {
					Node<E> node = visitedLeft.pop();
					itemHandler.accept(node);
					if (node.hasRightChild()) {
						notVisitedLeft.push(node.right);
						isNextVisitedValid = false; // next in visited stack has not fully visited left tree
					}
				} else {
					Node<E> node = notVisitedLeft.pop();
					visitedLeft.push(node);
					if (node.hasLeftChild()) {
						notVisitedLeft.push(node.left);
					} else { // no left child = left subtree has been traversed
						isNextVisitedValid = true;
					}
				}
			}
		}
		protected void recursiveTraverseInOrder(Consumer<Node<E>> itemHandler) {
			if (this.left != null) { this.left.recursiveTraverseInOrder(itemHandler); }
			itemHandler.accept(this);
			if (this.right != null) { this.right.recursiveTraverseInOrder(itemHandler); }
		}
		protected void iterativeTraversePostOrder(Consumer<Node<E>> itemHandler) {
			Deque<Node<E>> visited = new ArrayDeque<>();
			iterativeTraversePreOrder(visited::push); // reverse of preorder
			while (visited.size() > 0) { itemHandler.accept(visited.pop()); }
		}
		protected void recursiveTraversePostOrder(Consumer<Node<E>> itemHandler) {
			if (this.left != null) { this.left.recursiveTraversePostOrder(itemHandler); }
			if (this.right != null) { this.right.recursiveTraversePostOrder(itemHandler); }
			itemHandler.accept(this);
		}
	}

	private Node<E> root;
	private int size;

	public SimpleBST() {
		root = null;
		size = 0;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public int height() {
		if (root == null) { return -1; }
		return root.recursiveGetHeight();
	}

	@Override
	public boolean contains(E item) {
		return root != null && root.iterativeContains(item);
	}

	@Override
	// mutator should update count
	public boolean add(E item) {
		boolean success;
		if (root == null) {
			root = new Node(item);
			success = true;
		} else {
			success = root.iterativeAdd(item);
		}
		if (success) { this.size++; }
		return success;
	}

	@Override
	// mutator should update count
	public boolean remove(E item) {
		if (root == null) { return false; }
		Node target = root.recursiveFindNode(item);
		if (target == null) { return false; }
		Node replacement = target.removeSelf();
		if (target == root) { root = replacement; }
		this.size--;
		return true;
	}

	@Override
	public E successor(E item) {
		if (root == null) { return null; }

		Node<E> target = root;
		Node<E> nextLargerParent = null;
		while (target != null && !target.val.equals(item)) { // get insertion point
			if (item.compareTo(target.val) < 0) {
				nextLargerParent = target;
				target = target.left;
			} else {
				target = target.right;
			}
		}
		if (target != null) { target = target.successor(); }
		// target has no successor in it's subtree
		if (target == null) { target = nextLargerParent; }
		return target.val;
	}

	@Override
	public E predecessor(E item) {
		if (root == null) { return null; }

		Node<E> target = root;
		Node<E> nextSmallerParent = null;
		while (target != null && !target.val.equals(item)) { // get insertion point
			if (item.compareTo(target.val) < 0) {
				target = target.left;
			} else {
				nextSmallerParent = target;
				target = target.right;
			}
		}
		if (target != null) { target = target.predecessor(); }
		// target has no predecessor in it's subtree
		if (target == null) { target = nextSmallerParent; }
		return target.val;
	}

	@Override
	public E get(int index) {
		return inOrder().get(index);
	}

	@Override
	public int indexOf(E item) {
		return inOrder().indexOf(item);
	}

	@Override
	public void traversePreOrder(Consumer<E> itemHandler) {
		if (root != null) { root.iterativeTraversePreOrder((node) -> itemHandler.accept(node.val)); }
	}

	@Override
	public void traverseInOrder(Consumer<E> itemHandler) {
		if (root != null) { root.iterativeTraverseInOrder((node) -> itemHandler.accept(node.val)); }
	}

	@Override
	public void traversePostOrder(Consumer<E> itemHandler) {
		if (root != null) { root.iterativeTraversePostOrder((node) -> itemHandler.accept(node.val)); }
	}
}
