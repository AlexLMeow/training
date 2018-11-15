package trees;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface BinaryTree<E> {

	int size();

	/**
	 * Height of binary tree: number of edges to get from root to furthest leaf.
	 * Empty tree: -1
	 * Root only: 0
	 */
	int height();

	/**
	 * Retrieves item at given 0-based index of the in-order sequence.
	 * @throws IndexOutOfBoundsException
	 */
	E get(int index);

	/**
	 * Retrieves index of some occurrence of E in tree, else -1 if not found
	 */
	int indexOf(E item);

	boolean contains(E item);

	/**
	 * Finds the in-order successor in the tree. Returns null if no successor.
	 */
	E successor(E item);

	/**
	 * Finds the in-order predecessor in the tree. Returns null if no successor.
	 */
	E predecessor(E item);

	/**
	 * Inserts an item into tree and returns true if insertion was successful.
	 * Insertion may fail if for example, inserting duplicate vals into a tree only allowing unique values.
	 */
	boolean add(E item);

	/**
	 * Removes item from tree. Returns true if item was found and removed, false if item not in tree.
	 */
	boolean remove(E item);

	void traversePreOrder(Consumer<E> itemHandler);
	default List<E> preOrder() {
		List<E> ordered = new ArrayList<>();
		traversePreOrder(ordered::add);
		return ordered;
	}

	void traverseInOrder(Consumer<E> itemHandler);
	default List<E> inOrder() {
		List<E> ordered = new ArrayList<>();
		traverseInOrder(ordered::add);
		return ordered;
	}

	void traversePostOrder(Consumer<E> itemHandler);
	default List<E> postOrder() {
		List<E> ordered = new ArrayList<>();
		traversePostOrder(ordered::add);
		return ordered;
	}
}
