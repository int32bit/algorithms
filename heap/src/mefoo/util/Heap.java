import java.util.*;
public class Heap<E> {
	transient private Object[] data;
	private int size = 0;
	private final Comparator<? super E> comparator;
	transient private int modCount = 0;
	private boolean isMaxHeap = true;
	private int maxSize = 10;
    private boolean ulimit = false;
    private static final int DEFAULT_SIZE = 11;
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private Heap(int maxSize, boolean isMaxHeap, Comparator<? super E> comparator) {
		this.maxSize = maxSize;
		data = new Object[maxSize];
		this.isMaxHeap = isMaxHeap;
		this.comparator = comparator;
        this.ulimit = false;
    }
	private Heap(int maxSize, boolean isMaxHeap) {
        this(maxSize, isMaxHeap, null);
	}
    private Heap(boolean isMaxHeap, Comparator<? super E> comparator) {
        this.isMaxHeap = isMaxHeap;
        this.comparator = comparator;
        this.ulimit = true;
        data = new Object[DEFAULT_SIZE];
    }
    private Heap(boolean isMaxHeap) {
        this(isMaxHeap, null);
    }
    /**
     * 获取一个TopK容器，该容器保存排前K大的容器，小于前K大的数，直接丢弃。
     * @param expectedCount 容器大小
     * @param comparator 对象比较器
     * @return 保存前K大的数据容器实例。
     * @see #getMaxFilter(int)
     */
    public static <T> Heap<T> getMaxFilter(int expectedCount, Comparator<? super T> comparator) {
        return new Heap<T>(expectedCount, false, comparator);
    }
    /**
     * 获取一个TokK容器，该容器保存排前K大的容器，小于前K大的数，直接丢弃。
     * 元素需要实现Comparable接口
     * @param expectedCount 保存前K大的数据容器实例。
     * @return 保存前K大的数据容器实例。
     * @see #getMaxFilter(int, Comparator)
     */
    public static <T> Heap<T> getMaxFilter(int expectedCount) {
        return new Heap<T>(expectedCount, false);
    }
    /**
     * 获取一个TopK容器，该容器保存排前K小的容器，大于前K小的数，直接丢弃。
     * @param expectedCount 容器大小
     * @param comparator 对象比较器
     * @return 保存前K小的数据容器实例。
     * @see #getMinFilter(int)
     */
    public static <T> Heap<T> getMinFilter(int expectedCount, Comparator<? super T> comparator) {
        return new Heap<T>(expectedCount, true, comparator);
    }
    /**
     * 获取一个TokK容器，该容器保存排前K小的容器，大于前K大的数，直接丢弃。
     * 元素需要实现Comparable接口
     * @param expectedCount 保存前K小的数据容器实例。
     * @return 保存前K小的数据容器实例。
     * @see #getMaxFilter(int, Comparator)
     */
    public static <T> Heap<T> getMinFilter(int expectedCount) {
        return new Heap<T>(expectedCount, true);
    }
    /**
     * 获取一个大根堆
     * @param comparator 对象比较器
     * @return 大根堆实例
     */
    public static <T> Heap<T> getMaxHeap(Comparator<? super T> comparator) {
        return new Heap<T>(true, comparator);
    }
    /**
     * 获取一个大根堆， 元素需要实现Comparable接口
     * @return 大根堆实例
     */
    public static <T> Heap<T> getMaxHeap() {
        return new Heap<T>(true);
    }
    /**
     * 获取一个小根堆
     * @param comparator 对象比较器
     * @return 小根堆实例
     */
    public static <T> Heap<T> getMinHeap(Comparator<? super T> comparator) {
        return new Heap<T>(false, comparator);
    }
    /**
     * 获取一个小根堆， 元素需要实现Comparable接口
     * @return 小根堆实例
     */
    public static <T> Heap<T> getMinHeap() {
        return new Heap<T>(false);
    }
	@SuppressWarnings("unchecked")
	private int cmp(Object e1, Object e2) {
		int c = 0;
		if (comparator != null)
			c = comparator.compare((E)e1, (E)e2);
		else
			c = ((Comparable<? super E>)e1).compareTo((E)e2);
		return isMaxHeap ? c : -c;
	}
    private void grow(int minCapacity) {
        int oldCapacity = data.length;
        int newCapacity = oldCapacity + ((oldCapacity < 64) ?
                    (oldCapacity + 2) : (oldCapacity >> 1));
        if (newCapacity > MAX_ARRAY_SIZE)
            throw new OutOfMemoryError();
        data = Arrays.copyOf(data, newCapacity);
    }
    private boolean addAnyWay(E x) {
        modCount++;
        int i = size;
        if (i >= data.length)
            grow(i + 1);
        size = i + 1;
        if (i == 0)
            data[0] = x;
        else {
            siftUp(i, x);
        }
        return true;
    }
    /**
     * 往容器中添加一个元素
     * @param x 需要添加的元素
     * @return 成功返回true，失败返回false
     */
	public boolean add(E x) {
		if (x == null)
			throw new NullPointerException();
		if (size == 0) {
			data[0] = x;
			size++;
			return true;
		}
        if (ulimit || size < maxSize) {
            return addAnyWay(x);
        }
		if (cmp(x, data[0]) >= 0)
			return false;
		else {
            siftDown(0, x);
			return true;
		}
	}
	/**
	 * 向本容器中添加指定容器的所有元素。
	 * @param c 指定的容器。
	 * @return 本容器实例被修改，返回true，否则返回false
	 */
    public boolean addAll(Collection<? extends E> c) {
        boolean isModify = false;
        for (E e : c)
            isModify |= add(e);
        return isModify;
    }
    @SuppressWarnings("unchecked")
    private E popAnyWay() {
        //assert(size > 0);
        int s = --size;
        modCount++;
        E result = (E)data[0];
        E x = (E)data[s];
        data[s] = null;
        if (s > 0)
            siftDown(0, x);
        return result;
    }
    /**
     * 弹出根元素
     * @return 若容器为空，返回null，否则返回根元素
     */
	public E pop() {
        if (!ulimit) {
            throw new UnsupportedOperationException("The Filter heap can't not remove any element"); 
        }
		if (size == 0)
			return null;
        return popAnyWay();
	}
	/**
	 * 获取根元素值。
	 * @return 若容器为空，返回null，否则返回根元素
	 */
    @SuppressWarnings("unchecked")
    public E peak() {
        if (size == 0)
            return null;
        return (E)data[0];
    }
	private void siftUp(int pos, E value) {
		while (pos > 0) {
			int parent = (pos - 1) >>> 1;
			Object e = data[parent];
			if (cmp(e, value) >= 0)
				break;
			data[pos] = e;
			pos = parent;
		}
		data[pos] = value;
	}
	private void siftDown(int pos, E value) {
		int half = size >>> 1;
		while (pos < half) {
			int leftIndex = (pos << 1) + 1;
			int rightIndex = leftIndex + 1;
			int maxIndex = leftIndex;
			Object maxValue = data[maxIndex];
			if (rightIndex < size && cmp(data[rightIndex], maxValue) > 0) {
				maxIndex = rightIndex;
				maxValue = data[rightIndex];
			}
			if (cmp(value, maxValue) >= 0)
				break;
			data[pos] = maxValue;
			pos = maxIndex; 
		}
		data[pos] = value;
	}
	public Object[] toArray() {
		return Arrays.copyOf(data, size);
	}
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		final int size = this.size;
		if (a.length < size)
			return (T[])Arrays.copyOf(data, size, a.getClass());
		System.arraycopy(data, 0, a, 0, size);
		if (a.length > size)
			a[size] = null;
		return a;
	}
	/**
	 * 测试该容器是否为空。
	 * @return 容器为空，返回true，否则返回false。
	 */
    public boolean isEmpty() {
        return size == 0;
    }
    /**
     * 返回当前容器可以容纳的最大元素数量
     * @return 当前容器可以容纳的最大元素数量。
     */
    public int capacity() {
        return data.length;
    }
    /**
     * 清空容器
     */
    public void clear() {
        modCount++;
        for (int i = 0; i < size; ++i)
            data[i] = null;
        size = 0;
    }
    public int size() {
        return size;
    }
    /**
     * 测试对象是否在容器中。
     * @param o 需要测试的对象
     * @return 存在容器返回true，否则false。
     */
    public boolean contains(Object o) {
        for (int i = 0; i < size; ++i)
            if (data[i].equals(o))
                return true;
        return false;
    } 
}
