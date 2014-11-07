import java.util.Comparator;


class Node<E> {
	Object[] values;
	Node<E>[] children;
	Node<E> parent;
	boolean isLeaf;
	int size;
	final Comparator<E> comparator;
	Node(int order, Comparator<E> comparator) {
		this.values = new Object[order];
		this.isLeaf = true;
		this.size = 0;
		this.comparator = comparator;
	}
	Node(int order) {
		this(order, null);
	}
	private int cmp(E e1, E e2) {
		if (comparator != null)
			return comparator.compare(e1, e2);
		@SuppressWarnings("unchecked")
		Comparable<E> c1 = (Comparable<E>)e1;
		return c1.compareTo(e2);
	}
	@SuppressWarnings("unchecked")
	int find(E key) {
		int low = 0;
		int high = size - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			int cmp = cmp(key, (E)values[mid]);
			if (cmp == 0)
				return mid;
			if (cmp < 0) {
				high = mid - 1;
			} else {
				low = mid + 1;
			}
		}
		return -(low + 1);
	}
	int insertIndex(E key) {
		return -find(key) - 1;
	}
	int insertToLeaf(E key) {
		int index = insertIndex(key);
		if (index < 0)
			return index;
		for (int i = size; i > index; --i) {
			values[i] = values[i - 1];
		}
		values[index] = key;
		size++;
		return index;
	}
	int insert(E key, Node<E> left, Node<E> right) {
		int index = insertIndex(key);
		if (index < 0)
			return index;
		for (int i = size; i > index; --i) {
			values[i] = values[i - 1];
			children[i + 1] = children[i];
		}
		children[index] = left;
		children[index + 1] = right;
		values[index] = key;
		size++;
		return index;
	}
	void print() {
		for (int i = 0; i < size; ++i)
			System.out.print(values[i] + " ");
		System.out.println();
	}
}
