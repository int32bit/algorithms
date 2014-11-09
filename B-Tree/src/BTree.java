import java.util.*;

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
	private int modCount;
	/**
	 * 创建一个m阶B树。
	 * @param order B树的阶
	 * @param comparator 关键字比较器。
	 */
	public BTree(int order, Comparator<E> comparator) {
		//this.root = new Node<E>();
		this.order = order;
		this.comparator = comparator;
		this.MAX_KEYS = order - 1;
		this.MIN_KEYS = (order + 1) / 2 - 1;
		this.height = 0;
		this.size = 0;
	}
	/**
	 * 创建一个m阶B树。
	 * @param order B树的阶。
	 */
	public BTree(int order) {
		this(order, null);
	}
	public BTree(List<E> list, int order, Comparator<E> comparator) {
		this(order, comparator);
		E extra = prepare(list, order); // 排重、排序、如果刚好整除order，还需要剔除一个元素，必须要求最后一个叶子关键字数量了少于order
		this.size = list.size();
		if (size > 0) {
			initFromList(list);
			if (extra != null)
				add(extra);
		}
	}
	public BTree(List<E> list, int order) {
		this(list, order, null);
	}
	public static <T> BTree<T> build(Collection<T> c, int order, Comparator<T> comparator) {
		List<T> list;
		if (c instanceof List) {
			list = (List<T>)c;
		} else {
			list = new ArrayList<T>(c.size());
			list.addAll(c);
		}
		return new BTree<T>(list, order, comparator);
	}
	public static <T> BTree<T> build(Collection<T>c, int order) {
		return build(c, order, null);
	}
	private E prepare(List<E> list, int order) {
		// 排重
		Set<E> set = new HashSet<>(list.size());
		set.addAll(list);
		list.clear();
		list.addAll(set);
		// 排序
		sort(list);
		E extra = null;
		if (!list.isEmpty() && list.size() % order == 0) {
			extra = list.remove(list.size() - 1);
		}
		return extra;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void sort(List<E> list) {
		if (list == null || list.size() < 2)
			return;
		if (comparator != null) {
			Collections.sort(list, comparator);
		} else {
			Collections.sort((List<Comparable>)list);
		}
	}
	@SuppressWarnings( "unchecked")
	private void initFromList(List<E> list) {
		// 构造叶子节点，要求最后一个叶子关键字必须少于order，其余叶子关键字数量必须等于order
		int n = (list.size() - 1 + order) / order;
		Node<E>[] nodes = new Node[n];
		for (int i = 0; i < n; ++i) {
			nodes[i] = new Node<E>();
			for (int j = 0; j < order; ++j) {
				if (i * order + j < list.size()) {
					nodes[i].values[j] = list.get(i * order + j);
					nodes[i].size++;
				}
			}
		}
		this.height = 1;
		// make internal nodes;
		int lastN;
		while (true) {
			height ++;
			lastN = n;
			int m = 0;
			for (int i = 0; i < n; ++i) {
				if (nodes[i].size == order) {
					m++;
				}
			}
			if (m < 1)
				break;
			n = (m - 1 + order) / order;
			Node<E>[]p = new Node[n];
			for (int i = 0; i < n; ++i) {
				p[i] = new Node<E>();
				p[i].children = new Node[order + 1];
				p[i].isLeaf = false;
				for (int j = 0; j < order; ++j) {
					int index = i * order + j;
					if (index >= lastN)
						break;
					int size = nodes[index].size;
					if (size < order) {
						break;
					}
					p[i].values[j] = nodes[index].values[size - 1]; // 取最后一个节点作为上层节点的alue
					nodes[index].values[size - 1] = null;
					p[i].size++; // 上层节点数量+1
					nodes[index].size--; // 下层节点数量-1
					nodes[index].parent = p[i]; // 更新下层节点的父亲节点指向上层节点
					p[i].children[j] = nodes[index]; // 更新上层节点的的孩子节点指向内部节点
				}
			}
			// 更新最后一个节点，上层节点的最后一个孩子指向下层的最后一个节点，下层最后一个节点的parent指向上层最后一个节点
			Node<E> newLast = p[n - 1];
			Node<E> oldLast;
			//下层最后一个节点取决于最后一个节点关键字数量是否order
			if (newLast.children[newLast.size - 1] == nodes[lastN - 1]) {
				oldLast = nodes[lastN - 1].children[order];
			} else {
				oldLast = nodes[lastN - 1];
			}
			newLast.children[newLast.size] = oldLast;
			oldLast.parent = newLast;
			// 更新工作节点。
			nodes = p;
		}
		this.root = nodes[0];
		//if (root.size > MAX_KEYS)
		//	split(root);
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
	 * 当且仅当指定容器的元素全都在此树中，返回true，否则返回false。
	 * @param c 指定的容器。
	 * @return 若容器的所有元素都在此树中，返回true，否则返回false。
	 */
	public boolean containsAll(Collection<? extends E> c) {
		for (E e : c) {
			if (!contains(e))
				return false;
		}
		return true;
	}
	/**
	 * 插入关键字key到B-tree中。
	 * @param key 待插入的关键字。
	 * @return 若已经存在关键字，则插入失败，返回false， 否则返回true。
	 */
	public boolean add(E key) {
		if (key == null) {
			return false;
		}
		if (root == null) {
			root = new Node<E>();
			this.height = 1;
			this.size = 0;
		}
		boolean inserted = insert(key, root);
		if (inserted) {
			++size;
			++modCount;
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
			parent = new Node<E>();
			parent.isLeaf = false; // 设置为非叶子节点
			root = parent; // 更新root节点
			height++; // 高度加1
			parent.children = new Node[order + 1]; // 创建孩子节点，由于是先插入，再分裂，实际空间要大一
		}
		int mid = (p.size - 1) >>> 1;
		Node<E> left = new Node<E>(); // 分裂，创建一个新的空节点
		Node<E> right = p; // 右边节点为原来的节点
		left.isLeaf = p.isLeaf; // 节点是否叶子，取决于分裂前是否叶子。
		if (!left.isLeaf) { // 若不是叶子，则还需要为孩子分配空间。
			left.children = new Node[order + 1];
		}
		// 更新孩子节点的parent指针
		// FIXME
		if (!p.isLeaf) {
			p.children[mid].parent = parent.parent; // 由于中间节点需要上调，它的父亲节点也需要指向它爷爷节点。
			for (int i = 0; i <= mid; ++i) { // 左子树的孩子应该指向左子树。
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
			left.children[i] = right.children[mid];
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
			right.children[j] = right.children[right.size]; // 更新最后一个孩子节点, 注意奇数j == mid，但偶数不是。。
		}
		right.size = right.size - mid - 1; // 更新右子树关键字数量
		left.parent = parent; // 把子树的父亲节点更新
		right.parent = parent;
		if (parent.size > MAX_KEYS) // 如果父亲节点也达到最大关键字数量，需要递归分裂。
			split(parent);
	}
	/**
	 * 把指定容器的所有元素加入到树中。
	 * @param c 指定的容器，容器的元素将加入到此树中。
	 * @return 若调用此方法引起了B-tree的改变，返回true，否则返回false，表示没有成功添加任何元素
	 */
	public boolean addAll(Collection<? extends E> c) {
		boolean isModify = false;
		for (E e : c) {
			isModify |= add(e);
		}
		return isModify;
	}
	public boolean remove(E e) {
		if (root == null) {
			return false;
		}
		boolean isRemoved = remove(e, root);
		if (isRemoved) {
			--size;
			++modCount;
		}
		return isRemoved;
	}
	private boolean remove(E e, Node<E> p) {
		if (p.isLeaf) {
			boolean isRemoved = p.deleteFromLeaf(e);
			if (p.size < MIN_KEYS) {
				mergeOrRotate(p);
			}
			return isRemoved;
		}
		return false;
	}
	private Node<E> leftBrother(Node<E> p) {
		if (p == null || p.parent == null)
			return null;
		Node<E> parent = p.parent;
		int i;
		for (i = 0; i <= parent.size; ++i) {
			if (parent.children[i] == p)
				break;
		}
		if (i >= 1)
			return parent.children[i - 1];
		return null;
	}
	private Node<E> rightBrother(Node<E> p) {
		if (p == null || p.parent == null)
			return null;
		Node<E> parent = p.parent;
		int i;
		for (i = 0; i <= parent.size; ++i) {
			if (parent.children[i] == p)
				break;
		}
		if (i < parent.size) {
			return parent.children[i + 1];
		}
		return null;
	}
	private void mergeOrRotate(Node<E> p) {
		Node<E> parent = p.parent;
		if (parent == null) { // 说明p是root节点，不需要处理
			return;
		}
		Node<E> left = leftBrother(p); // 获取左兄弟
		if (left != null && left.size > MIN_KEYS) { // 左兄弟还很富裕，向左兄弟借一个。
			
		}
		Node<E> right = leftBrother(p); // 左兄弟也贫穷，还好右兄弟富裕，借右兄弟。
		if (right != null && right.size > MIN_KEYS) {
			
		}
		// 左右兄弟都没有节点，需要合并。
	}
	public void print() {
		print(root);
	}
	private void print(Node<E> p) {
		if (p == null)
			return;
		System.out.println(p);
		if (!p.isLeaf) {
			for (int i = 0; i <= p.size; ++i) {
				print(p.children[i]);
			}
		}
	}
	/**
	 * 把B-tree转化成Object数组。
	 * @return 转化后的数组，数组包含所有关键字。
	 */
	public Object[] toArrays() {
		List<E> values = new ArrayList<>(size);
		dumpToList(root, values);
		return values.toArray();
	}
	/**
	 * 把B-tree的关键字转化成有序数组。
	 * @param arr 需要转化的空数组，需要预先分配空间。
	 * @return 转化后的数组，数组拥有B-tree的所有关键字。
	 */
	public  E[] toArrays(E[]arr) {
		List<E> values = new ArrayList<>(size);
		dumpToList(root, values);
		return values.toArray(arr);
	}
	/**
	 * 把子树p的所有值dump到List中，保持值有序。
	 * @param p 子树
	 * @param values 需要导入的列表
	 */
	@SuppressWarnings("unchecked")
	private void dumpToList(Node<E> p, List<E> values) {
		if (p == null) {
			return;
		}
		if (p.isLeaf) {
			for (int i = 0; i < p.size; ++i) {
				values.add((E)p.values[i]);
			}
		} else {
			for (int i = 0; i < p.size; ++i) {
				dumpToList(p.children[i], values);
				values.add((E)p.values[i]);
			}
			dumpToList(p.children[p.size], values);
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
	 * 判断该B树是否为空
	 * @return 如果B树为空，返回true，否则返回false
	 */
	public boolean isEmpty() {
		return size == 0;
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
		Node() {
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
			//if (index + 1 >= children.length) {
			//	print();
			//	System.out.printf("%s : %s\n", this, key);
			//}
			children[index + 1] = right;
			values[index] = key;
			size++;
			return index;
		}
		boolean deleteFromLeaf(T key) {
			int index = binarySearch(key);
			if (index < 0)
				return false;
			for (int i = index; i < size; ++i) {
				values[i] = values[i + 1]; // 由于多分配了一个空间，访问i + 1应该没有问题
			}
			this.size--;
			return true;
		}
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("[");
			for (int i = 0; i < size; ++i) {
				sb.append(values[i] + ",");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append(']');
			return sb.toString();
		}
	}
	public static void main(String[] args) {
		BTree<Integer> tree;
		Set<Integer> set = new HashSet<>();
		Random r = new Random();
		//int n = Integer.parseInt(args[0]);
		int n = r.nextInt(100);
		System.out.println("n = " + n);
		for (int i = 1; i <= n; ++i)
			set.add(r.nextInt(100));
		//tree = new BTree<Integer>(list, 4);
		tree = BTree.build(set, 3);
		//tree.addAll(set);
		tree.print();
		System.out.println(tree.toArrays().length);
		System.out.println(set.size());
		for (int i : set) {
			if (!tree.contains(i)) {
				System.out.println("ERROR:" + i);
			}
		}
	}
}
