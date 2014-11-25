import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
public class RBTree<E> {
	private Node<E> root;
	private int modCount;
	private int size;
	private final Comparator<E> comparator;
	private static final boolean RED = false;
	private static final boolean BLACK = true;
	public RBTree(Comparator<E> comparator) {
		this.comparator = comparator;
		this.size = 0;
	}
	public RBTree() {
		this(null);
	}
	public int size() {
		return size;
	}
	public int cmp(E e1, E e2) {
		if (comparator != null)
			return comparator.compare(e1, e2);
		@SuppressWarnings("unchecked")
		Comparable<? super E> o = (Comparable<? super E>)e1;
		return o.compareTo(e2);
	}
	public boolean contains(E e) {
		return find(e) != null;
	}
	public boolean isEmpty() {
		return size == 0;
	}
	public boolean add(E e) {
		Objects.requireNonNull(e);
		Node<E> t = root;
		if (t == null) {
			cmp(e, e);
			root = new Node<E>(e, null);
			size = 1;
			modCount++;
			return true;
		}
		int cmp;
		Node<E> parent;
		do {
			parent = t;
			cmp = cmp(e, t.value);
			if (cmp < 0)
				t = t.left;
			else if (cmp > 0)
				t = t.right;
			else
				return false;
		} while(t != null);
		Node<E> newNode = new Node<>(e, parent);
		if (cmp < 0)
			parent.left = newNode;
		else
			parent.right = newNode;
		fixAfterInsertion(newNode);
		size++;
		modCount++;
		return true;
	}
	public boolean remove(E e) {
		Node<E> removed = find(e);
		if (removed == null)
			return false;
		deleteNode(removed);
		return true;
	}
	protected Node<E> find(E e) {
		Objects.requireNonNull(e);
		Node<E> p = root;
		while (p != null) {
			int cmp = cmp(e, p.value);
			if (cmp < 0)
				p = p.left;
			else if (cmp > 0)
				p = p.right;
			else
				return p;
		}
		return null;
	}
	private final static class NodeWithLevel<E> {
		Node<E> node;
		int level;
		NodeWithLevel(Node<E> node, int level) {
			this.node = node;
			this.level = level;
		}
		Node<E> getNode() {
			return node;
		}
		int getLevel() {
			return level;
		}
	}
	public void print() {
		print(' ', "\n");
	}
	public void print(char elementDelimiter, String lineDelimiter) {
		if (root != null) {
			LinkedList<NodeWithLevel<E>> list = new LinkedList<>();
			list.addLast(new NodeWithLevel<>(root, 1));
			int level = 1;
			StringBuilder sb = new StringBuilder();
			while (!list.isEmpty()) {
				NodeWithLevel<E> e = list.removeFirst();
				Node<E> curNode = e.getNode();
				int curLevel = e.getLevel();
				if (level != curLevel) {
					sb.deleteCharAt(sb.length() - 1);
					sb.append(lineDelimiter);
					level = curLevel;
				}
				sb.append(e.getNode().toString() + elementDelimiter);
				if (leftOf(curNode) != null)
					list.addLast(new NodeWithLevel<>(leftOf(curNode), curLevel + 1));
				if (rightOf(curNode) != null)
					list.addLast(new NodeWithLevel<>(rightOf(curNode), curLevel + 1));
			}
			if (sb.length() > 0)
				sb.deleteCharAt(sb.length() - 1);
			System.out.print(sb);
		}
	}
	public List<E> toList() {
		List<E> list = new ArrayList<>(size);
		dumpToList(root, list);
		return list;
	}
	private void dumpToList(Node<E> p, List<E> list) {
		if (p == null)
			return;
		if (p.left != null)
			dumpToList(p.left, list);
		list.add(p.getValue());
		if (p.right != null)
			dumpToList(p.right, list);
	}
	static final class Node<E> {
		E value;
		Node<E> left;
		Node<E> right;
		Node<E> parent;
		boolean color = BLACK;
		Node(E value, Node<E> parent) {
			this.value = value;
			this.parent = parent;
		}
		public E getValue() {
			return value;
		}
		public E setValue(E e) {
			E oldValue = value;
			value = e;
			return oldValue;
		}
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Node)) {
				return false;
			}
			Node<?> e = (Node<?>)o;
			return e.value.equals(value);
		}
		@Override
		public String toString() {
			String color = this.color == RED ? "red" : "black";
			return color + "(" + value + ")";
			//return value.toString();
		}
		@Override
		public int hashCode() {
			int hash = (value == null ? 0 : value.hashCode());
			return hash;
		}
	}
	final Node<E> getFirstNode() {
		Node<E> p = root;
		if (p != null)
			while(p.left != null)
				p = p.left;
		return p;
	}
	final Node<E> getLastNode() {
		Node<E> p = root;
		if (p != null)
			while (p.right != null)
				p = p.right;
		return p;
	}
	private void deleteNode(Node<E> p) {
		modCount++;
		size--;
		// ｐ有两个孩子节点，则使用p的后继代替，然后转化成删除后继节点（后继必然没有左孩子）
		if (p.left != null && p.right != null) {
			Node<E> s = successor(p);
			p.value = s.value;
			p = s;
		}
		Node<E> replacement = (p.left != null ? p.left : p.right);
		// p有一个孩子节点，则用它的孩子节点替换当前节点
		if (replacement != null) {
			replacement.parent = p.parent;
			if (p.parent == null)// p is root
				root = replacement;
			else if (p == p.parent.left)
				p.parent.left = replacement;
			else
				p.parent.right = replacement;
			p.left = p.right = p.parent = null;
			if (p.color == BLACK)
				fixAfterDeletion(replacement);
		} else if (p.parent == null) { // p没有孩子节点且p是root，则直接删除之。
			root = null;
		} else { // p没有孩子节点情况, 把自己当作replacement然后删除之。
			if (p.color == BLACK)
				fixAfterDeletion(p);
			if (p.parent != null) {
				if (p == p.parent.left)
					p.parent.left = null;
				else if (p == p.parent.right)
					p.parent.right = null;
				p.parent = null;
			}
		}
	}
	static <E> Node<E> successor(Node<E> t) {
		if (t == null)
			return null;
		if (t.right != null) {
			Node<E> p = t.right;
			while (p.left != null)
				p = p.left;
			return p;
		} else {
			Node<E> p = t.parent;
			Node<E> ch = t;
			while (p != null && ch == p.right) {
				ch = p;
				p = p.parent;
			}
			return p;
		}
	}
	static <E> Node<E> predecessor(Node<E> t) {
		if (t == null)
			return null;
		if (t.left != null) {
			Node<E> p = t.left;
			while (p.right != null)
				p = p.right;
			return p;
		} else {
			Node<E> p = t.parent;
			Node<E> ch = t;
			while (p != null && ch == p.left) {
				ch = p;
				p = p.parent;
			}
			return p;
		}
	}
	private static <E> boolean colorOf(Node<E> p) {
		return (p == null ? BLACK : p.color);
	}
	private static <E> void setColor(Node<E> p, boolean c) {
		if (p != null)
			p.color = c;
	}
	private static <E> Node<E> parentOf(Node<E> p) {
		return (p == null ? null : p.parent);
	}
	private static <E> Node<E> leftOf(Node<E> p) {
		return (p == null ? null : p.left);
	}
	private static <E> Node<E> rightOf(Node<E> p) {
		return (p == null ? null : p.right);
	}
	private void rotateLeft(Node<E> p) {
		if (p != null) {
			Node<E> r = p.right;
			p.right = r.left;
			if (r.left != null)
				r.left.parent = p;
			r.parent = p.parent;
			if (p.parent == null)
				root = r;
			else if (p.parent.left == p)
				p.parent.left = r;
			else
				p.parent.right = r;
			r.left = p;
			p.parent = r;
		}
	}
	private void rotateRight(Node<E> p) {
		if (p != null) {
			Node<E> l = p.left;
			p.left = l.right;
			if (l.right != null)
				l.right.parent = p;
			if (p.parent == null)
				root = l;
			else if (p.parent.right == p) 
				p.parent.right = l;
			else
				p.parent.left = l;
			l.right = p;
			p.parent = l;
		}
	}
	private void fixAfterInsertion(Node<E> x) {
		setColor(x, RED);
		// case 1: x是根节点
		if (x == root) {
			x.color = BLACK;
			return;
		}
		// case 2： x的父亲节点是黑色节点，已经满足条件。
		if (x.parent.color == BLACK) {
			return;
		}
		// 
		while (x != null && x != root && x.parent.color == RED) { // x是红色节点，并且x的父亲也是红色节点
			Node<E> parent = parentOf(x);
			Node<E> grandparent = parentOf(parent); // grandparent node
			if (parent == leftOf(grandparent)) {
				Node<E> uncle = rightOf(grandparent); // uncle node
				// case 3：父亲节点和叔父节点都是红色节点，则重绘父亲节点和叔父节点为黑色节点，祖父节点为红色节点，从祖父节点继续调整
				if (colorOf(uncle) == RED) {
					setColor(parent, BLACK);
					setColor(uncle, BLACK);
					setColor(grandparent, RED);
					x = grandparent;
				} else {
					// case 4：父亲节点是红色节点，并且叔父节点是黑色节点或者缺失，x是右子树，则对父亲节点一次左旋转，此时必然会出现case5
					if (x == rightOf(parent)) {
						x = parent;
						rotateLeft(x);
					}
					// case 5: 父亲节点是红色节点，并且叔父节点是黑色或者缺失，x是左子树，则设置父亲节点为黑色节点，祖父节点为红色节点，然后对
					// 祖父节点一次右旋转。继续对x进行调整。
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					rotateRight(parentOf(parentOf(x)));
				}
			} else {
				Node<E> uncle = leftOf(grandparent);
				//case 3
				if (colorOf(uncle) == RED) {
					setColor(parent, BLACK);
					setColor(uncle, BLACK);
					setColor(grandparent, RED);
					x = grandparent;
				} else {
					//case 4
					if (x == leftOf(parent)) {
						x = parent;
						rotateRight(x);
					}
					// case 5
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					rotateLeft(parentOf(parentOf(x)));
				}
			}
		}
		root.color = BLACK;
	}
	private void fixAfterDeletion(Node<E> x) {
		while (x != root && colorOf(x) == BLACK) {
			// x 是左孩子情况
			if (x == leftOf(parentOf(x))) {
				Node<E> sib = rightOf(parentOf(x));
				/* 兄弟节点是红色节点，这种情形下我们在N的父亲上做左旋转，
				 * 把红色兄弟转换成N的祖父，我们接着对调N的父亲和祖父的颜色。
				 * 完成这两个操作后，尽管所有路径上黑色节点的数目没有改变，
				 * 但现在N有了一个黑色的兄弟和一个红色的父亲（它的新兄弟是黑色因为它是红色S的一个儿子）
				 */
				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(x), RED);
					rotateLeft(parentOf(x));
					sib = rightOf(parentOf(x)); // 此时兄弟节点必为黑色节点。
				}
				if (colorOf(leftOf(sib)) == BLACK && colorOf(rightOf(sib)) == BLACK) {
					setColor(sib, RED);
					x = parentOf(x);
				} else {
					if (colorOf(rightOf(sib)) == BLACK) {
						setColor(leftOf(sib), BLACK);
						setColor(sib, RED);
						rotateRight(sib);
						sib = rightOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), BLACK);
					setColor(rightOf(sib), BLACK);
					rotateLeft(parentOf(x));
					x = root;
				}
			} else {
				Node<E> sib = leftOf(parentOf(x));
				 if (colorOf(sib) == RED) {
	                    setColor(sib, BLACK);
	                    setColor(parentOf(x), RED);
	                    rotateRight(parentOf(x));
	                    sib = leftOf(parentOf(x));
	                }

	                if (colorOf(rightOf(sib)) == BLACK &&
	                    colorOf(leftOf(sib)) == BLACK) {
	                    setColor(sib, RED);
	                    x = parentOf(x);
	                } else {
	                    if (colorOf(leftOf(sib)) == BLACK) {
	                        setColor(rightOf(sib), BLACK);
	                        setColor(sib, RED);
	                        rotateLeft(sib);
	                        sib = leftOf(parentOf(x));
	                    }
	                    setColor(sib, colorOf(parentOf(x)));
	                    setColor(parentOf(x), BLACK);
	                    setColor(leftOf(sib), BLACK);
	                    rotateRight(parentOf(x));
	                    x = root;
	                }
			}
		}
		setColor(x, BLACK);
	}
}
