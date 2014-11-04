package mefoo.util;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Objects;

public class BitSet extends AbstractSet<Integer> {
	private long[] words;
	private int size;
	private int wordsInUse = 0;
	private final static int ADDRESS_BITS_PER_WORD = 6;
	private final static int BITS_PER_WORD = 1 << ADDRESS_BITS_PER_WORD;
	private static final int NOT_EXISTS = -1234;
	protected transient int modCount;

	private int wordIndex(int bitIndex) {
		return bitIndex >> ADDRESS_BITS_PER_WORD;
	}

	/**
	 * Given a bit index, return word index containing it.
	 */
	private void initWords(int nbits) {
		words = new long[wordIndex(nbits - 1) + 1];
	}

	/**
	 * Ensures that the BitSet can hold enough words
	 * 
	 * @param wordsRequired
	 *            the minimum acceptable number of words
	 */
	private void ensureCapacity(int wordsRequired) {
		if (words.length < wordsRequired) {
			// Allocate larger of doubled size or required size.
			int request = Math.max(2 * words.length, wordsRequired);
			words = Arrays.copyOf(words, request);
		}
	}

	/**
	 * Ensures that the BitSet can accommodate a given wordIndex. temporarily
	 * violating the invariants. The caller must restore the invariants before
	 * returning to the user, possibly using recalculatedWordsInUse().
	 * 
	 * @param wordIndex
	 *            the index to be accommodated.
	 */
	private void expandTo(int wordIndex) {
		int wordsRequired = wordIndex + 1;
		if (wordsInUse < wordsRequired) {
			ensureCapacity(wordsRequired);
			wordsInUse = wordsRequired;
		}
	}

	private void checkInvariants() {
		assert (wordsInUse == 0 || words[wordsInUse - 1] != 0);
		assert (wordsInUse >= 0 || wordsInUse <= words.length);
		assert (wordsInUse == words.length || words[wordsInUse] == 0);
	}

	/**
	 * Sets the field wordsInUse to the logical size in words of the bit set.
	 */
	private void recalculateWordsInUse() {
		int i;
		for (i = wordsInUse - 1; i >= 0; --i)
			if (words[i] != 0)
				break;
		wordsInUse = i + 1;
	}
	/**
	 * Creates a bit set whose initial size is large enough to explicitly
	 * represent bits with indices in range {@code 0} through
	 * {@code maxNumber-1}. All bits are initially {@code 0}.
	 * @param maxNumber The max expected number.
	 * @throws NegativeArraySizeException if the specified initial size
	 * 			is negative. 
	 */
	public BitSet(int maxNumber) {
		if (maxNumber < 0) {
			throw new NegativeArraySizeException("maxNumber < 0: " + maxNumber);
		}
		initWords(maxNumber);
		size = 0;
	}
	/**
	 * Creates a new bit set. Allocate one word default.
	 */
	public BitSet() {
		this(BITS_PER_WORD);
	}
	/**
	 * Returns a new bit set containing all the integer in the given arguments.
	 * @param ints the given ints will be contained in the new BitSet
	 * @return a {@code BitSet} containing all the integer in the {@code ints}
	 */
	public static BitSet valueOf(Integer ...ints) {
		int max = -1;
		for (int i : ints) {
			if (i < 0) {
				throw new RuntimeException("The element < 0:" + i);
			}
			max = i > max ? i : max;
		}
		BitSet bitset = new BitSet(max);
		for (int i : ints) {
			bitset.add(i);
		}
		return bitset;
	}
	/**
	 * Returns <tt>true</tt> if this set contains the specified elements.
	 * @param o element whose presence in this set to be tested
	 * @return <tt>true</tt> if this set contains the specified element
	 */
	@Override
	public boolean contains(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof Integer))
			return false;
		Integer i = (Integer) o;
		if (i < 0)
			throw new RuntimeException("i < 0: " + i);
		checkInvariants();
		int wordIndex = wordIndex(i);
		return (wordIndex < wordsInUse)
				&& ((words[wordIndex] & (1L << i)) != 0);
	}
	/**
	 * Adds the specified element to this set if it is not already present and the element >= 0.
	 * @param e element to be added to this set
	 * @return <tt>true</tt> if this set did not already contain the specified
	 * element and the element >= 0
	 */
	@Override
	public boolean add(Integer e) {
		if (e < 0)
			throw new RuntimeException("To add a negative number: " + e);
		if (contains(e)) {
			return false;
		}
		int wordIndex = wordIndex(e);
		expandTo(wordIndex);
		words[wordIndex] |= (1L << e);
		++size;
		++modCount;
		return true;
	}
	/**
	 * Removes the specified element from this set if it is present.
	 * @param o object to be removed from this set, if present
	 * @return <tt>true</tt> if the set contained the specified element
	 */
	@Override
	public boolean remove(Object o) {
		if (!(o instanceof Integer)) {
			return false;
		}
		Integer i = (Integer) o;
		if (i < 0)
			return false;
		if (!contains(i)) {
			return false;
		}
		int wordIndex = wordIndex(i);
		if (wordIndex >= wordsInUse)
			return false;
		words[wordIndex] &= ~(1L << i);
		--size;
		modCount++;
		recalculateWordsInUse();
		checkInvariants();
		return true;
	}
	/**
	 * Returns The number of elements in this set.
	 * @return the number of elements in the set
	 */
	@Override
	public int size() {
		return size;
	}
	@Override
	public boolean equals(Object o) {
		if (o.getClass() != getClass()) {
			return false;
		}
		BitSet other = (BitSet)o;
		if (size() != other.size() || this.wordsInUse != other.wordsInUse) {
			return false;
		}
		return Arrays.equals(words, other.words);
	}
	@Override
	public int hashCode() {
		long h = 1234;
		for (int i = wordsInUse; i >= 0; --i) {
			h ^= words[i] * (i + 1);
		}
		return (int)((h >> 32) ^ h);
	}
	public int capacity() {
		return words.length << ADDRESS_BITS_PER_WORD;
	}
	/**
	 * Removes all of the elements from this set.
	 * The set will be empty after this call returns.
	 */
	@Override
	public void clear() {
		while(wordsInUse > 0)
			words[--wordsInUse] = 0;
		size = 0;
	}
	/**
	 * Returns a Integer array contains all of the elements in set.
	 * @return a Integer array contains all of the elements in set.
	 */
	@Override
	public Integer[] toArray() {
		Integer[] array = new Integer[size];
		int cur = 0;
		for (int i = 0; i < wordsInUse; ++i) {
			long word = words[i];
			for (int j = 0; word != 0 && j < BITS_PER_WORD; ++j) {
				if ((word & 1L) != 0) {
					array[cur++] = i * BITS_PER_WORD + j;
				}
				word >>= 1;
			}
		}
		return array;
	}
	/**
	 * Returns the next element from this index.
	 * @param index the index from.
	 * @return the next element
	 */
	private int next(int index) {
		int realIndex = index >= 0 ? index : 0;
		int wordIndex = wordIndex(realIndex);
		for (int i = wordIndex; i < wordsInUse; ++i) {
			long word = words[i];
			for (int j = 0; j < BITS_PER_WORD && word != 0; ++j) {
				if ((word & 1L) != 0) {
					int value = i * BITS_PER_WORD + j;
					/** value should be larger then index **/
					if (value > index)
						return value;
				}
				word >>= 1;
			}
		}
		return NOT_EXISTS;
	}
	/**
	 * Returns an iterator over the elements in this set.
	 * @return an Iterator over the elements in this set
	 * @see ConcurrentModificationException
	 */
	@Override
	public Iterator<Integer> iterator() {
		return new BitSetIterator();
	}
	/**
	 * Checks each elements in this set, if it is not contained in the
	 * specified collection, removed from this set.
	 * @param c the specified collection
	 * @return <tt>true</tt> if succeed to remove all of the elements which
	 * exclude in the specified collection.
	 * @see #remove(Object)
	 * @see #contains(Object)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		Objects.requireNonNull(c);
		boolean modified = false;
		for (int i : (Integer[]) toArray()) {
			if (!c.contains(i)) {
				remove(i);
				modified = true;
			}
		}
		return modified;
	}
	/**
	 * Removes All of the elements from this set if the element contained in the
	 * specified collection.
	 * @param c the specified collection
	 * @return <tt>true</tt> if succeed to remove all elements contained in the
	 * specified collection.
	 * @see #remove(Object)
	 * @see #contains(Object)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		Objects.requireNonNull(c);
		boolean modified = false;
		for (Object o : c) {
			modified |= remove(o);
		}
		return modified;
	}

	private class BitSetIterator implements Iterator<Integer> {
		int cursor = -1;
		int lastRet = -1;
		int expectedModCount = modCount;

		@Override
		public void remove() {
			if (lastRet < 0) {
				throw new IllegalStateException();
			}
			checkForComodification();
			try {
				BitSet.this.remove(lastRet);
				cursor = lastRet;
				lastRet = -1;
				expectedModCount = modCount;
			} catch (IndexOutOfBoundsException e) {
				throw new ConcurrentModificationException();
			}
		}

		@Override
		public Integer next() {
			checkForComodification();
			cursor = BitSet.this.next(cursor);
			lastRet = cursor;
			return cursor;
		}

		@Override
		public boolean hasNext() {
			int next = BitSet.this.next(cursor);
			return next != NOT_EXISTS;
		}

		final void checkForComodification() {
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
		}
	}
	/**
	 * Test if this set has no element.
	 * @return <tt>true</tt> if this set contains no element.
	 * @see #size()
	 */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}
	@Override
	public String toString() {
		return Arrays.toString(toArray());
	}
	/**
	 * Returns the max elements in this set.
	 * @return the max elements in this set.
	 * @see #getMin()
	 * @throws RuntimeException if this set is empty
	 */
	public int getMax() {
		if (size == 0) {
			throw new RuntimeException("Can't get any element from the empty set");
		}
		long word = words[wordsInUse - 1];
		long mask = 1L << BITS_PER_WORD - 1;
		int i;
		for (i = BITS_PER_WORD - 1; i >= 0; --i) {
			if ((word & mask) != 0) {
				break;	
			}
			mask >>= 1;
		}
		return (wordsInUse - 1) * BITS_PER_WORD + i;
	}
	/**
	 * Returns the min elements in this set.
	 * @return the min elements in this set
	 * @see #getMax()
	 * @throws RuntimeException if it is empty.
	 */
	public int getMin() {
		if (size == 0) {
			throw new RuntimeException("Can't get any element from the empty set");
		}
		return next(-1);
	}
}
