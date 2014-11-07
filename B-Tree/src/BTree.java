import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * B-tree树，实现查询、插入、删除。
 * 关键字需要实现Comparable接口或者传递Comparator。
 * @author fgp
 *
 * @param <E> 关键字类型。
 */
public class BTree<E> {
	private Node<E> root;
	private final Comparator<E> comparator;
	private final int MAX_KEYS;
	private final int MIN_KEYS;
	private final int order;
	private int height;
	private int size;
	/**
	 * 创建一个m阶B树。
	 * @param order B树的阶
	 * @param comparator 关键字比较器。
	 */
	public BTree(int order, Comparator<E> comparator) {
		this.root = new Node<E>(order);
		this.order = order;
		this.comparator = comparator;
		this.MAX_KEYS = order - 1;
		this.MIN_KEYS = (order + 1) / 2 - 1;
		this.height = 1;
		this.size = 0;
	}
	/**
	 * 创建一个m阶B树。
	 * @param order B树的阶。
	 */
	public BTree(int order) {
		this(order, null);
	}
	@SuppressWarnings("unchecked")
	private int cmp(Object e1, Object e2) {
		if (comparator != null)
			return comparator.compare((E)e1, (E)e2);
		Comparable<E> c1 = (Comparable<E>)e1;
		return c1.compareTo((E)e2);
	}
	/**
	 * 检测关键字是否在B-tree中
	 * @param key 需要检测的关键字。
	 * @return 存在返回true，否则返回false
	 */
	public boolean contains(E key) {
		if (key == null)
			return false;
		return contains(key, root);
	}
	/**
	 * 检测关键字是否在p子树中
	 * @param key 需要检测的关键字
	 * @param p 需要检测的子树
	 * @return 存在返回true，不存在返回false
	 */
	private boolean contains(E key, Node<E> p) {
		if (p == null)
			return false;
		if (p.isLeaf) { // 如果是叶子节点，则直接二分搜索是否存在。
			return p.binarySearch(key) >= 0;
		}
		Node<E>[] children = p.children;
		int index = p.insertIndex(key); // 不是叶子节点，若插入位置< 0，说明存在于内部节点中，否则往子树继续查询。
		if (index < 0)
			return true;
		return contains(key, children[index]);
	}
	/**
	 * 插入关键字key到B-tree中。
	 * @param key 待插入的关键字。
	 * @return 若已经存在关键字，则插入失败，返回false， 否则返回true。
	 */
	public boolean insert(E key) {
		if (key == null) {
			return false;
		}
		if (root == null) {
			root = new Node<E>(order);
			this.height = 1;
			this.size = 0;
		}
		boolean inserted = insert(key, root);
		if (inserted) {
			++size;
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 把关键字key插入到合适的位置。
	 * 注意：只能插入到叶子中，不能插入到内部节点中。
	 * @param key 待插入的关键字。
	 * @param p 需要插入的子树。
	 * @return 若关键字已经存在，插入失败，返回false，否则成功插入，返回true。
	 */
	private boolean insert(E key, Node<E> p) {
		assert(p != null);
		if (!p.isLeaf) { // 总是插入到叶子中，不可能直接插入到内部节点
			int index = p.insertIndex(key); // 获取插入位置，如果 < 0说明已存在
			if (index < 0) // index < 0 说明key已存在
				return false;
			return insert(key, p.children[index]); // 插入的位置就是孩子的位置
		}
		boolean inserted = p.insertToLeaf(key) >= 0; // p是叶子节点，直接插入。
		
		if (p.size > MAX_KEYS) { // 如果关键字多于最大关键字数量，需要分裂节点。
			split(p);
		}
		return inserted;
	}
	/**
	 * 节点关键字数量超过最大数量，需要分裂。
	 * @param p 需要分裂的节点。
	 */
	@SuppressWarnings("unchecked")
	private void split(Node<E> p) {
		//assert(p != null && p.size > MAX_KEYS);
		Node<E> parent = p.parent;
		if (parent == null) { // parent为null，即当前节点为root，需要上升高度
			parent = new Node<E>(order);
			parent.isLeaf = false; // 设置为非叶子节点
			root = parent; // 更新root节点
			height++; // 高度加1
			parent.children = new Node[order + 1]; // 创建孩子节点，由于是先插入，再分裂，实际空间要大一
		}
		int mid = p.size >>> 1;
		Node<E> left = new Node<E>(order); // 分裂，创建一个新的空节点
		Node<E> right = p; // 右边节点为原来的节点
		left.isLeaf = p.isLeaf; // 节点是否叶子，取决于分裂前是否叶子。
		if (!left.isLeaf) { // 若不是叶子，则还需要为孩子分配空间。
			left.children = new Node[order + 1];
		}
		// 更新孩子节点的parent指针
		// FIXME
		if (!p.isLeaf) {
			p.children[mid].parent = parent.parent; // 由于中间节点需要上调，它的父亲节点也需要指向它爷爷节点。
			for (int i = 0; i < mid; ++i) { // 左子树的孩子应该指向左子树。
				p.children[i].parent = left;
			}
			/* 本来就指向right，不需要更新。
			for (int i = mid + 1; i < p.size; ++i) {
				p.children[i].parent = right;
			}
			*/
		}
		parent.insertToNonLeaf((E)p.values[mid], left, right); // 把中间节点插入父节点。
		
		int i, j;
		for (i = 0; i < mid; ++i) { // 拷贝右子树信息到左子树。
			left.values[i] = right.values[i];
			if (!left.isLeaf) { /* 拷贝孩子信息 */
				left.children[i] = right.children[i];
			}
		}
		if (!left.isLeaf) {
			left.children[mid] = right.children[mid];
		}
		left.size = mid; // 更新左子树关键字数量
		// 删除右子树多余关键字和孩子，因为已经拷贝到左孩子中去了。
		for (i = mid + 1, j = 0; i < right.size; ++i, ++j) {
			right.values[j] = right.values[i];
			if (!right.isLeaf) {
				right.children[j] = right.children[i];
			}
		}
		if (!right.isLeaf) {
			right.children[mid] = right.children[right.size];
		}
		right.size = right.size - mid - 1; // 更新右子树关键字数量
		left.parent = parent; // 把子树的父亲节点更新
		right.parent = parent;
		if (parent.size > MAX_KEYS) // 如果父亲节点也达到最大关键字数量，需要递归分裂。
			split(parent);
	}
	public void print() {
		print(root);
	}
	private void print(Node<E> p) {
		if (p == null)
			return;
		for (int i = 0; i < p.size; i++) {
			System.out.print(p.values[i] + " ");
		}
		System.out.println();
		if (!p.isLeaf) {
			for (int i = 0; i <= p.size; ++i) {
				print(p.children[i]);
			}
		}
	}
	/**
	 * 返回B树的阶
	 * @return B树的阶
	 */
	public int getOrder() {
		return this.order;
	}
	/**
	 * 返回B树的高度，高度从1开始。
	 * @return B树的高度。
	 */
	public int getHeight() {
		return this.height;
	}
	/**
	 * 返回关键字的数量
	 * @return 关键字数量。
	 */
	public int size() {
		return this.size;
	}
	/**
	 * 返回B树一个节点能够容纳的最大关键字数量，等于阶数-1
	 * @return B树一个节点能够容纳的最大关键字数量。
	 */
	public int getMaxKeys() {
		return this.MAX_KEYS;
	}
	/**
	 * 返回B树除了根节点以外其余任意一个节点最少的关键字数量。等于「阶数/2」-1
	 * @return B树除了根节点以外其余任意一个节点最少的关键字数量
	 */
	public int getMinKeys() {
		return this.MIN_KEYS;
	}
	/**
	 * B-tree树节点
	 * @author fgp
	 *
	 * @param <T> 节点存储关键字的类型。
	 */
	final class Node<T> {
		Object[] values;
		Node<T>[] children;
		Node<T> parent;
		boolean isLeaf;
		int size;
		Node(int order) {
			this.values = new Object[order];
			this.isLeaf = true;
			this.size = 0;
		}
		/**
		 * 在内部二分查找关键字。
		 * @param key 需要查找的关键字。
		 * @return 若关键字存在，返回位置。否则不存在，假设插入的位置为i，则返回-(i + 1).
		 */
		@SuppressWarnings("unchecked")
		int binarySearch(T key) {
			int low = 0;
			int high = size - 1;
			while (low <= high) {
				int mid = (low + high) >>> 1;
				int cmp = cmp(key, (T)values[mid]);
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
		/**
		 * 返回关键字插入的位置，但并不实际插入。
		 * @param key 需要插入的关键字。
		 * @return 若关键字不存在，返回插入的位置， 否则返回关键字的位置的相反数。
		 */
		int insertIndex(T key) {
			return -binarySearch(key) - 1;
		}
		/** 
		 * 插入关键字key到叶子中，不需要更新孩子 
		 * @param key 需要插入的关键字key
		 * @return 关键字不存在，则插入并返回插入的位置，否则返回已经存在的位置的相反数。
		 */
		int insertToLeaf(T key) {
			int index = insertIndex(key);
			if (index < 0)
				return index;
			for (int i = size; i > index; --i) {// 移动向右key
				values[i] = values[i - 1];
			}
			values[index] = key;
			++size;
			return index;
		}
		/**
		 * 插入关键字到非叶子节点中，即内部节点，需要同时更新孩子节点。
		 * 插入的情况只有在孩子分裂的时，因此孩子分成左右两个子树。
		 * @param key 需要插入的关键字
		 * @param left 孩子分裂后的左子树。
		 * @param right 孩子分裂后的右子树。
		 * @return 若关键字不存在，返回插入的位置。若关键字存在，返回关键字位置。
		 */
		int insertToNonLeaf(T key, Node<T> left, Node<T> right) {
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
	}
	public static void main(String[] args) {
		BTree<Integer> tree = new BTree<>(5);
		List<Integer> list = new ArrayList<>();
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < 1000; ++i)
			list.add(r.nextInt(1000));
		for (int i : list)
			tree.insert(i);
		for (int i : list) {
			if (!list.contains(i)) {
				System.err.println("ERROR " + i);
				break;
			}
		}
		System.out.println(tree.getHeight());
		System.out.println(tree.size());
	}
}
