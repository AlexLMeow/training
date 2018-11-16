package other_structures;
import java.util.*;
public class LRUCache {

	class ListNode {
		int key;
		int value;
		ListNode newer;
		ListNode older;
		ListNode(int key, int v) {
			this.key = key;
			value = v;
		}
		void removeSelf() {
			if (newer != null) newer.older = older;
			if (older != null) older.newer = newer;
			newer = null;
			older = null;
		}
		@Override
		public String toString() {
			return "" + key + " -> " + value;
		}
	}

	ListNode newest = null;
	ListNode oldest = null;
	Map<Integer, ListNode> store;
	int size = 0;
	int cap;

	public LRUCache(int capacity) {
		store = new HashMap<>(capacity);
		cap = capacity;
	}

	public String useOrder() {
		ListNode node = newest;
		if (node == null) {
			return "EMPTY";
		} else {
			StringBuilder sb = new StringBuilder();
			while (node != null) {
				sb.append(node.key).append(" < ");
				node = node.older;
			}
			return sb.toString();
		}
	}

	public int get(int key) {
		if (cap <= 0) return -1;
		if (store.containsKey(key)) {
			ListNode node = store.get(key);
			makeMostRecent(node);
			return node.value;
		} else {
			return  -1;
		}
	}

	// handles empty list ops
	void makeMostRecent(ListNode node) {
		if (newest == node) {
			return;
		}
		if (node == oldest && node.newer != null) {
			oldest = node.newer; // update oldest if necessary
		}
		node.removeSelf();
		if (newest != null) {
			newest.newer = node;
		}
		node.older = newest;
		newest = node;
		if (oldest == null) {
			oldest = node;
		}
	}

	// handles empty list ops
	void invalidateOldest() {
		ListNode node = oldest;
		if (node != null) { // nonempty list
			if (newest == oldest) { // single element list
				newest = null;
				oldest = null;
			} else {
				oldest = node.newer;
				node.removeSelf();
			}
			store.remove(node.key);
			size--;
		}
	}

	public void put(int key, int value) {
		if (cap <= 0) { return; }
		ListNode node = store.get(key);
		if (node == null) { // not found, need to expand
			if (size == cap) { // invalidate oldest
				invalidateOldest();
			}
			node = new ListNode(key, value);
			makeMostRecent(node);
			store.put(key, node);
			size++;
		} else {
			makeMostRecent(node);
			node.value = value; // update value
		}
	}
	public static void main(String... args) {
		Scanner in = new Scanner(System.in);
		LRUCache cache = new LRUCache(in.nextInt());
		in.nextLine();
		System.out.println(cache.useOrder());
		String input = in.nextLine();
		while (!input.equals("quit")) {
			String[] tokens = input.split(" ");
			switch (tokens[0]) {
				case "put":
					cache.put(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
					break;
				case "get":
					System.out.println(cache.get(Integer.parseInt(tokens[1])));
					break;
			}

			System.out.println(cache.useOrder());
			input = in.nextLine();
		}

	}
}

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 *
 * recent
 * 4 -> 3
 * 2 -> 1
 *
 * 3 -> 2
 */
