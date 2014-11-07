import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class BTree<E> {
	private Node<E> root;
	private final Comparator<E> comparator;
	private final int MAX_KEYS;
	private final int MIN_KEYS;
	private final int order;
	private int height;
	public BTree(int order, Comparator<E> comparator) {
		this.root = new Node<E>(order, comparator);
		this.order = order;
		this.comparator = comparator;
		this.MAX_KEYS = order - 1;
		this.MIN_KEYS = (order + 1) / 2 - 1;
		this.height = 1;
	}
	public BTree(int order) {
		this(order, null);
	}
	public boolean contains(E key) {
		return search(key, root);
	}
	private boolean search(E key, Node<E> p) {
		if (p == null)
			return false;
		if (p.isLeaf) {
			return p.find(key) >= 0;
		}
		Node<E>[] children = p.children;
		int index = p.insertIndex(key);
		if (index < 0)
			return true;
		return search(key, children[index]);
	}
	public boolean insert(E key) {
		if (root == null) {
			root = new Node<E>(order, comparator);
			this.height = 1;
		}
		return insert(key, root);
	}
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
	@SuppressWarnings("unchecked")
	private void split(Node<E> p) {
		assert(p != null && p.size > MAX_KEYS);
		Node<E> parent = p.parent;
		if (parent == null) { // parent为null，即当前节点为root，需要上升高度
			parent = new Node<E>(order, comparator);
			parent.isLeaf = false; // 设置为非叶子节点
			root = parent; // 更新root节点
			height++; // 高度加1
			parent.children = new Node[order + 1]; // 创建孩子节点，由于是先插入，再分裂，实际空间要大一
		}
		int mid = p.size >>> 1;
		Node<E> left = new Node<E>(order, comparator); // 分裂，创建一个空节点
		Node<E> right = p; // 右边节点为原来的节点
		left.isLeaf = p.isLeaf;
		if (!left.isLeaf) { /* 如果待分裂节点不是叶子，那么分裂后也不是叶子 */
			left.children = new Node[order + 1];
		}
		// 更新孩子节点的parent指针
		// FIXME
		if (!p.isLeaf) {
			p.children[mid].parent = parent.parent;
			for (int i = 0; i < mid; ++i) {
				p.children[i].parent = left;
			}
			for (int i = mid + 1; i < p.size; ++i) {
				p.children[i].parent = right;
			}
		}
		parent.insert((E)p.values[mid], left, right); // 把中间节点插入父节点， 并获取它插入的位置。
		
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
	public int getOrder() {
		return this.order;
	}
	public int getHeight() {
		return this.height;
	}
	public static void main(String[] args) {
		BTree<Integer> tree = new BTree<>(3);
		Integer[] arr = new Integer[]{9,3,4,2,10,0,-1,11,6,12,13,7,-2};
		List<Integer> list = new ArrayList<>();
		list.add(9);
		list.add(3);
		list.add(4);
		list.add(2);
		list.add(10);
		list.add(0);
		list.add(-1);
		list.add(11);
		list.add(6);
		list.add(12);
		list.add(13);
		list.add(7); 
		list.add(-2);                                                                                                                                                               
		for (int i : list) {
			tree.insert(i);
		}
		tree.print();
		
		for (int i : list) {
			if (!tree.contains(i)) {
				System.err.println("ERROR: " + i);
				break;
			}
		}
	}
}
