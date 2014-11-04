package fugp.test;

import java.util.Arrays;
import java.util.Iterator;

import fugp.util.BitSet;

public class BitSetTest {
	public static void main(String[] args) {
		/*BitSet bitset = BitSet.valueOf(0,1,2,3,4,4, 128, 1, 127, 129);
		bitset.addAll(Arrays.asList(99, 100, 102));
		bitset.removeAll(Arrays.asList(1, 2, 3, 4));
		assert(bitset.containsAll(Arrays.asList(127, 129, 128, 99, 100, 102)));
		System.out.println("bitset: " + bitset);
		System.out.println("max: " + bitset.getMax());
		System.out.println("min: " + bitset.getMin());
		System.out.println("size: " + bitset.size());
		System.out.println("capacity: " + bitset.capacity());
		System.out.print("iterator: [");
		Iterator<?> iter = bitset.iterator();
		while (iter.hasNext()) {
			System.out.print(iter.next() + ", ");
		}
		System.out.println("]");*/
		BitSet bitset = new BitSet();
		assert(bitset.isEmpty());
		assert(bitset.size() == 0);
	}
}
