package treap;

import java.util.Comparator;
import java.util.Random;
/**
 * A treap implemented by java
 * @author fgp
 *
 * @param <E> the type of elements
 */
public class Treap<E> {
	private int size;
	private int modCount;
	private Node<E> root;
	private final Comparator<E> comparator;
	public Treap(Comparator<E> comparator) {
		this.comparator = comparator;
	}
	public Treap() {
		this(null);
	}
	public int size() {
		return size;
	}
	public boolean contains(E e) {
		return find(e) != null;
	}
	private int cmp(E e1, E e2) {
		if (comparator != null)
			return comparator.compare(e1, e2);
		@SuppressWarnings("unchecked")
		Comparable<? super E> c = (Comparable<? super E>)e1;
		return c.compareTo(e2);
	}
	/**
	 * Returns the Node whose value equals specified value
	 * @param e the specified value to find
	 * @return Node if it exists, or null
	 */
	private Node<E> find(E e) {
		Node<E> p = root;
		while (p != null) {
			if (cmp(e, p.value) == 0)
				return p;
			if (cmp(e, p.value) < 0) {
				p = p.left;
			} else {
				p = p.right;
			}
		}
		return p;
	}
	private void leftRotate(Node<E> p) {
		if (p != null) {
			Node<E> r = p.right;
			p.right = r.left;
			if (p.right != null)
				p.right.parent = p;
			r.parent = p.parent;  // Do not forget it!
			if (p == root)
				root = r;
			else {
				if (p.parent.left == p)
					p.parent.left = r;
				else
					p.parent.right = r;
			}
			r.left = p;
			p.parent = r;
		}
	}
	private void rightRotate(Node<E> p) {
		if (p != null) {
			Node<E> l = p.left;
			p.left = l.right;
			if (p.left != null)
				p.left.parent = p;
			l.parent = p.parent;
			if (p == root)
				root = l;
			else {
				if (p.parent.left == p)
					p.parent.left = l;
				else
					p.parent.right = l;
			}
			l.right = p;
			p.parent = l;
		}
	}
	private void rebalance(Node<E> p) {
		while (p != root) {
			Node<E> parent = p.parent;
			if (p.getPriority() < parent.getPriority()) {
				if (p == parent.left)
					rightRotate(parent);
				else
					leftRotate(parent);
				//p = p.parent; Do not need to update p;
			} else {
				return;
			}
		}
	}
	private Node<E> createNode(E e, int priority) {
		if (priority >= 0)
			return new Node<E>(e, priority);
		else
			return new Node<E>(e);
	}
	public boolean add(E e) {
		return add(e, -1);
	}
	public boolean add(E e, int priority) {
		if (root == null) {
			root = createNode(e, priority);
			size = 1;
			++modCount;
			return true;
		}
		Node<E> p = root;
		while (p != null) {
			if (cmp(e, p.value) == 0)
				return false;
			if (cmp(e, p.value) < 0) {
				if (p.left == null) {
					Node<E> n = createNode(e, priority);
					p.left = n;
					n.parent = p;
					p = n;
					break;
				} else 
					p = p.left;
			} else {
				if (p.right == null) {
					Node<E> n = createNode(e, priority);
					p.right = n;
					n.parent = p;
					p = n;
					break;
				} else 
					p = p.right;
			}
		}
		++size;
		++modCount;
		rebalance(p);
		return true;
	}
	private Node<E> getMinChild(Node<E> p) {
		assert(!isLeaf(p));
		if (p.left == null)
			return p.right;
		if (p.right == null)
			return p.left;
		if (p.left.priority <= p.right.priority)
			return p.left;
		else
			return p.right;
	}
	public boolean remove (E e) {
		Node<E> p = find(e);
		if (p == null)
			return false;
		while (!isLeaf(p)) {
			Node<E> candicate = getMinChild(p);
			if (candicate == p.left)
				rightRotate(p);
			else
				leftRotate(p);
		}
		removeFromLeaf(p);
		++modCount;
		--size;
		return true;
	}
	private boolean isLeaf(Node<E> p) {
		return p.left == null && p.right == null;
	}
	public void removeFromLeaf(Node<E> p) {
		p.value = null; // Let's GC work!
		if (p == root) {
			root = null;
		} else {
			if (p.parent.left == p)
				p.parent.left = null;
			else
				p.parent.right = null;
		}
	}
	static class Node<E> {
		E value;
		Node<E> left;
		Node<E> right;
		Node<E> parent;
		static final Random random = new Random();
		private final int priority;
		public Node(E value, int priority) {
			this.value = value;
			this.priority = priority >= 0 ? priority : -priority;
		}
		public Node(E value) {
			this(value, random.nextInt());
		}
		public int getPriority() {
			return priority;
		}
		@Override
		public String toString() {
			return value.toString() + ": " + getPriority();
		}
	}
	/*
	static class NodeWithLevel<E> {
		Node<E> node;
		int level;
		public NodeWithLevel(Node<E> value, int level) {
			this.node = value;
			this.level = level;
		}
	}
	void print() {
		if (root == null)
			return;
		LinkedList<NodeWithLevel<E>> queue = new LinkedList<>();
		queue.add(new NodeWithLevel<E>(root, 0));
		int level = 0;
		while (!queue.isEmpty()) {
			NodeWithLevel<E> p = queue.removeFirst();
			if (level != p.level) {
				System.out.println();
				level = p.level;
			}
			System.out.print(p.node + " ");
			if (p.node.left != null)
				queue.addLast(new NodeWithLevel<E>(p.node.left, p.level + 1));
			if (p.node.right != null)
				queue.addLast(new NodeWithLevel<E>(p.node.right, p.level + 1));
		}
		System.out.println();
	}
	*/
	
}
