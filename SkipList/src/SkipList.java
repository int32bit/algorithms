import java.util.ArrayList;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * SkipList implemented in java
 * @author fgp
 * @see List
 * @see ConcurrentSkipListSet
 * @param <E> the type of elements held in this list
 */
public class SkipList<E> implements Iterable<E> {
	
	private ArrayList<LinkedList<E>> list;
	private final Comparator<E> comparator;
	private int modCount = 0;
	private int size = 0;
	private static final int INIT_LEVEL = 5;
	private static final Random random = new Random();
	/**
	 * Constructs a SkipList with the Comparator of elements
	 * @param comparator the Comparator of the elements
	 */
	public SkipList(Comparator<E> comparator) {
		this.comparator = comparator;
		list = new ArrayList<>(INIT_LEVEL);
		list.add(new LinkedList<E>());
	}
	/**
	 * Constructs a empty list.
	 */
	public SkipList() {
		this(null);
	}
	private static <E> boolean isHeader(Node<E> p) {
		return p.getClass() == HeadNode.class;
	}
	private static <E> boolean isEnd(Node<E> p) {
		return p.getClass() == EndNode.class;
	}
	private int cmp(Node<E> p1, Node<E> p2) {
		
		if (isHeader(p1) || isEnd(p2)) {
			return -1;
		}
		if (comparator != null) {
			return comparator.compare(p1.value, p2.value);
		}
		@SuppressWarnings("unchecked")
		Comparable<? super E> c = (Comparable<? super E>)p1.value;
		return c.compareTo(p2.value);
	}
	private Node<E> find(E key) {
		Node<E> find = new Node<E>(key);
		int cur = list.size() - 1;
		Node<E> p = list.get(cur).getHeader();
		while (true) {
			while (cmp(find, p.next) > 0) {
				p = p.next;
			}
			if (cmp(find, p.next) == 0)
				return p.next;
			if (p.down != null)
				p = p.down;
			else {
				return null;
			}
				
		}
	}
	/**
	 * Returns the height of this list
	 * @return the height of this list
	 */
	public int height() {
		return list.size();
	}
	/**
	 * Returns true if this list contains the specified element
	 * @param e element whose presence in this list to be tested
	 * @return true if this list contains the specified element
	 */
	public boolean contains(E e) {
		Node<E> p = list.get(height() - 1).getHeader();
		Node<E> find = new Node<E>(e);
		while (true) {
			while (cmp(find, p.next) > 0)
				p = p.next;
			if (cmp(find, p.next) == 0)
				return true;
			if (p.down != null)
				p = p.down;
			else
				return false;
		}
	}
	private static int randomLevel() {
		int k = 1;
		while (random.nextBoolean()) {
			++k;
		}
		return k;
	}
	/**
	 * Inserts the specified element to this list
	 * @param e the element to add
	 * @return true if this list is changed after call this.
	 */
	public boolean add(E e) {
		int newLevel = randomLevel();
		int curLevel = height();
		@SuppressWarnings("unchecked")
		Node<E>[] last = new Node[curLevel];
		int cur = curLevel - 1;
		Node<E> p = list.get(cur).getHeader();
		Node<E> toAdd = new Node<E>(e);
		while (true) {
			while (cmp(toAdd, p.next) > 0) {
				p = p.next;
			}
			last[cur] = p;
			if (p.down != null) {
				p = p.down;
				cur--;
			} else {
				break;
			}
		}
		if (cmp(toAdd, p.next) == 0) {
			return false;
		}
		assert(cur == 0);
		Node<E> lastOne = null;
		for (int i = 0; i < newLevel; ++i) {
			Node<E> insertPosotion = null;
			if (i >= curLevel) {
				LinkedList<E> newList = new LinkedList<>(list.get(i - 1));
				list.add(newList);
				insertPosotion = newList.getHeader();
			} else {
				insertPosotion = last[i];
			}
			Node<E> newOne = new Node<E>(e);
			newOne.down = lastOne;
			lastOne = newOne;
			insertAt(insertPosotion, newOne);
		}
		++modCount;
		++size;
		return true;
	}
	/**
	 * Removes the specified element
	 * @param e the element to be removed
	 * @return true if  this list contained the specified element
	 */
	public boolean remove(E e) {
		Node<E> p = find(e);
		if (p == null)
			return false;
		while (p != null) {
			Node<E> down = p.down;
			removeNode(p);
			p = down;
		}
		--size;
		++modCount;
		return true;
	}
	private void removeNode(Node<E> p) {
		p.prev.next = p.next;
		p.next.prev = p.prev;
		p.value = null;
		p.next = null;
		p.prev = null;
		p.down = null;
	}
	/**
	 * Returns the number of elements in this list
	 * @return the number of elements in this list
	 */
	public int size() {
		return size;
	}
	private void insertAt(Node<E> p, Node<E> newNode) {
		newNode.next = p.next;
		newNode.prev = p;
		p.next.prev = newNode;
		p.next = newNode;
	}
	static class Node<E> {
		E value;
		Node<E> next;
		Node<E> prev;
		Node<E> down;
		Node() {
			
		}
		Node(E value) {
			this.value = value;
			this.next = this.prev = this.down = null;
		}
		@Override
		public String toString() {
			return value.toString();
		}
	}
	private final static class  HeadNode<E> extends Node<E> {
		@Override
		public String toString() {
			return "HEAD";
		}
	}
	private final static class EndNode<E> extends Node<E> {
		@Override
		public String toString() {
			return "END";
		}
	}
	static class LinkedList<E> {
		private Node<E> header;
		private Node<E> end;
		public LinkedList() {
			header = new HeadNode<>();
			end = new EndNode<>();
			header.next = end;
		}
		public LinkedList(LinkedList<E> down) {
			this();
			this.header.down = down.getHeader();
			this.end.down = down.getEnd();
		}
		public void insert(Node<E> position, Node<E> toInserted, Node<E> down) {
			toInserted.next = position.next;
			position.next.prev = toInserted;
			toInserted.prev = position;
			position.next = toInserted;
			toInserted.down = down;
		}
		public void insert(Node<E> position, Node<E> toInserted) {
			insert(position, toInserted, null);
		}
		public Node<E> getHeader() {
			return header;
		}
		public Node<E> getEnd() {
			return end;
		}
		@Override
		public String toString() {
			Node<E> p = header;
			StringBuilder sb = new StringBuilder();
			while (true) {
				if (isEnd(p)) {
					sb.append(p);
					break;
				} else {
					sb.append(p.toString() + "->");
					p = p.next;
				}
			}
			return sb.toString();
		}
	}
	void print() {
		int i = list.size() - 1;
		while (i >= 0) {
			System.out.println(list.get(i--));
		}
	}
	private Node<E> node(int index) {
		if (index < (size >> 1)) {
			Node<E> x = list.get(0).getHeader().next;
			for (int i = 0; i < index; ++i)
				x = x.next;
			return x;
		} else {
			Node<E> x = list.get(0).getEnd().prev;
			for (int i = size - 1; i > index; i--)
				x = x.prev;
			return x;
		}
	}
	private class ListItr implements ListIterator<E> {
		private Node<E> lastRet = null;
		private Node<E> next;
		private int nextIndex;
		private int expectedModCount = modCount;
		ListItr(int index) {
			next = (index == size) ? null : node(index);
			nextIndex = index;
		}
		@Override
		public void add(E arg0) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext() {
			return nextIndex < size;
		}

		@Override
		public boolean hasPrevious() {
			return nextIndex > 0;
		}

		@Override
		public E next() {
			checkForComodification();
			if (!hasNext())
				throw new NoSuchElementException();
			lastRet = next;
			next = next.next;
			nextIndex++;
			return lastRet.value;
		}

		@Override
		public int nextIndex() {
			return nextIndex;
		}

		@Override
		public E previous() {
			checkForComodification();
			if (!hasPrevious())
				throw new NoSuchElementException();
			lastRet = next = ( next == null) ? list.get(0).getEnd().prev : next.prev;
			nextIndex--;
			return lastRet.value;
		}

		@Override
		public int previousIndex() {
			return nextIndex - 1;
		}

		@Override
		public void remove() {
			checkForComodification();
			if (lastRet == null)
				throw new IllegalStateException();
			Node<E> lastNext = lastRet.next;
			SkipList.this.remove(lastRet.value);
			if (next == lastRet)
				next = lastNext;
			else
				nextIndex--;
			lastRet = null;
			expectedModCount++;
			
		}

		@Override
		public void set(E arg0) {
			throw new UnsupportedOperationException();
		}
		final void checkForComodification() {
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
		}	
	}
	@Override
	public Iterator<E> iterator() {
		return new ListItr(0);
	}
	public Iterator<E> reverseIterator() {
		final ListItr itr = new ListItr(size());
		return new Iterator<E>() {
			@Override
			public boolean hasNext() {
				return itr.hasPrevious();
			}

			@Override
			public E next() {
				return itr.previous();
			}

			@Override
			public void remove() {
				itr.remove();
			}
			
		};
	}
}
