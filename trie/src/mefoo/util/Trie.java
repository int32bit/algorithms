package mefoo.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Trie {
	private Node root;
	/**
	 * The number of entries in the tree.
	 */
	private int size = 0;
	private static final int CHARS_WIDTH = '~' - ' ' + 1;
	private static final int DEFAULT_MAX_WORD_LENGTH = 128;
	/**
	 * The max length of the word.
	 */
	private int maxWordLength;
	/**
	 * Constructors a new, empty trie with the specified max length of words. 
	 * @param maxWordLength the max length of words.
	 */
	public Trie(int maxWordLength) {
		root = new Node((char)0);
		this.maxWordLength = maxWordLength;
	}
	/**
	 * Constructors a new, empty trie with the default max word length 128.
	 */
	public Trie() {
		this(DEFAULT_MAX_WORD_LENGTH);
	}
	private int getIndex(char c) {
		return c - ' ';
	}
	private boolean invalid(String word) {
		if (word == null || word.length() < 1)
			return true;
		return word.length() > this.maxWordLength;
	}
	private boolean invalid(char c) {
		if (c >= ' ' && c <= '~') {
			return false;
		}
		return true;
	}
	/**
	 * traverse the trie to find if the specified word exists.
	 * @param word the word to find
	 * @return if it exists, return this node, or return null.
	 */
	private Node find(String word) {
		if (invalid(word)) {
			return null;
		}
		Node p = root;
		char[] array = word.toCharArray();
		int len = array.length;
		for (int i = 0; i < len; ++i) {
			Node[] w = p.children;
			if (w == null)
				return null;
			int j = getIndex(array[i]);
			if (w[j] == null)
				return null;
			p = w[j];
		}
		return p;
	}
	/**
	 * Check the word before add to trie.
	 * @param word the word to add to trie.
	 * @throws RuntimeException if the word is null or "".
	 * @throws ExceedMaxLengthException if the length of word larger than the max length.
	 */
	private void checkWordBeforeAdd(String word) {
		Objects.requireNonNull(word, "The null value can't be added");
		if (word.length() < 1) {
			throw new RuntimeException("The length of the word to be added should be > 0");
		}
		if (word.length() > this.maxWordLength) {
			throw new ExceedMaxLengthException("maxLength: " + this.maxWordLength
					+ " wordLength: " + word.length());
		}
	}
	/**
	 * Appends the specified word to the trie.
	 * @param word word to be append to this trie.
	 * @return <tt>true</tt>
	 */
	public boolean add(String word) {
		checkWordBeforeAdd(word);
		if (root == null)
			root = new Node();
		Node p = root;
		char[] array = word.toCharArray();
		int len = array.length;
		for (int i = 0; i < array.length; ++i) {
			char c = array[i];
			if (invalid(c)) {
				throw new IllegalCharactorException("illegal char: " + c);
			}
			int j = getIndex(c);
			if (p.children == null) {
				p.children = new Node[CHARS_WIDTH];
			}
			Node[] w = p.children;
			if (w[j] == null) {
				w[j] = new Node(c);
			}
			if (i == len - 1) {
				++w[j].count;
				++size;
			}
			p = w[j];
		}
		return true;
	}
	/**
	 * Appends all of words in the specified words.
	 * @param words collection containing words to added to this trie.
	 * @return <tt>true</tt> if this trie changed as a result of the call.
	 */
	public boolean addAll(Collection<String> words) {
		Objects.requireNonNull(words);
		boolean isModify = false;
		for (String word : words) {
			isModify |= add(word);
		}
		return isModify;
	}
	/**
	 * If the word is contained true is returned, otherwise false.
	 * @param word the word to check.
	 * @return true if the word is contained, otherwise false.
	 */
	public boolean contains(String word) {
		if (word == null || word.length() < 1)
			return false;
		Node p = find(word);
		if (p == null)
			return false;
		return p.count > 0;
	}
	/**
	 * If all words are so contained <tt>true</tt> is returned, otherwise
	 * <tt>false</tt>.
	 * @param words the words to check.
	 * @return true if all words are contained, otherwise false.
	 */
	public boolean containsAll(Collection<String> words) {
		for (String word : words) {
			if (!contains(word))
				return false;
		}
		return true;
	}
	/**
	 * Returns the number of elements in the trie equal to
	 * the specified word.
	 * @param word the word whose frequency is to be determined
	 * @return the number of elements in this trie equal to {@code word}
	 */
	public int count(String word) {
		Node p = find(word);
		if (p == null)
			return 0;
		return p.count;
	}
	/**
	 * remove the specified word.
	 * @param word the word to be remove.
	 * @param removeAll if true, remove all of this word,
	 * otherwise remove only one.
	 * @return if it removed successfully, return true.
	 */
	public boolean remove(String word, boolean removeAll) {
		Node p = find(word);
		if (p == null)
			return false;
		if (p.count < 1)
			return false;
		if (removeAll) {
			size -= p.count;
			p.count = 0;
		} else {
			size--;
			p.count--;
		}
		return false;
	}
	/**
	 * Remove all of the elements from trie equal to the word
	 * @param word the word to be removed
	 * @return true if removed successfully.
	 * @see #remove(String, boolean)
	 */
	public boolean remove(String word) {
		return remove(word, true);
	}
	/**
	 * Removes from this trie all of its elements that contained in the 
	 * specified collection.
	 * @param words collection containing words to be removed from this trie.
	 * @param removeAll if false, remove only one for a word, otherwise remove
	 * all of elements equals to the word.
	 * @return true if this trie changed as a result of the call.
	 * @see #remove(String, boolean)
	 */
	public boolean removeAll(Collection<String> words, boolean removeAll) {
		boolean status = true;
		for (String word : words) {
			if (!remove(word, removeAll)) {
				status = false;
			}
		}
		return status;
	}
	/**
	 * Removes from this trie all of its elements that contained in the
	 * specified collection.
	 * @param words the words to be removed.
	 * @return true if this trie changed as a result of the call.
	 */
	public boolean removeAll(Collection<String> words) {
		return removeAll(words, true);
	}
	/**
	 * Retains only the words in this trie that are contained in the
	 * specified collection.
	 * @param words collection containing words to re retained in this trie.
	 * @return @{code true} if this trie changed as a result of the call.
	 */
	public boolean retainAll(Collection<String> words) {
		List<String> ws = toList();
		boolean isModify = false;
		for (String word : ws) {
			if (!words.contains(word)) {
				isModify |= remove(word);
			}
		}
		return isModify;
	}
	/**
	 * Removed all of the words from this trie. The trie will
	 * be empty after this call returns. call this just set all
	 * the words reference to 0, do not release the resource.
	 * @see #destroy()
	 */
	public void clear() {
		clear(root);
	}
	private void clear(Node p) {
		if (p == null)
			return;
		if (p.children != null) {
			for (Node child : p.children) {
				clear(child);
			}
		}
		size -= p.count;
		p.count = 0;
	}
	/**
	 * Clear the trie, may also(depended on GC) release the resource allocated.
	 * @see #clear() 
	 */
	public void destroy() {
		destroy(root);
		size = 0;
	}
	private void destroy(Node p) {
		if (p == null)
			return;
		if (p.children != null) {
			for (Node child : p.children) {
				destroy(child);
			}
		}
		p.destory();
	}
	/**
	 * Checks if this trie is empty.
	 * @return true if it is empty, otherwise false.
	 */
	public boolean isEmpty() {
		return size == 0;
	}
	/**
	 * Returns the number of words in this trie.
	 * @return {@code int} presents the number of words in this trie.
	 */
	public int size() {
		return size;
	}
	/**
	 * Returns a array containing all of the words in this trie.
	 * @return a array containing all of the words.
	 */
	public String[] toArray() {
		return toList().toArray(new String[size]);
	}
	private List<String> toList() {
		List<String> values = new ArrayList<>(size);
		StringBuilder sb = new StringBuilder();
		traverse(root, sb, values);
		return Collections.unmodifiableList(values);
	}
	
	private void traverse(Node p, StringBuilder sb, Collection<String> values) {
		if (p == null)
			return;
		if (!invalid(p.value)) {
			sb.append(p.value);
		}
		for (int i = 0; i < p.count; ++i) {
			values.add(sb.toString());
		}
		if (p.children == null) {
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			return;
		}
		for (int i = 0; i < p.children.length; ++i) {
			traverse(p.children[i], sb, values);
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
	}
	static class Node {
		int count;
		char value;
		Node[] children;
		Node(char value) {
			this.value = value;
			this.count = 0;
		}
		Node() {
			this.value = 0;
			this.count = 0;
		}
		/**
		 * destroy this node
		 */
        public void destory() {
            this.count = 0;
            this.value = 0;
            if (children != null) {
            	for (int i = 0; i < children.length; ++i) {
            		children[i] = null; // clear to let GC do its work.
            	}
            }
            this.children = null;
        }
	}
}
