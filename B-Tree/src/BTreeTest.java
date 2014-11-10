import java.util.Iterator;


public class BTreeTest {
	public static void main(String[] args) {
		BTree<Integer> tree = new BTree<Integer>(5);
		for (int i = 0; i <= 100; i++) {
			tree.add(i);
		}
		tree.remove(50);
		assert(tree.contains(20)); // true
		System.out.println("Height: " + tree.getHeight()); // 4
		System.out.println("Size: " + tree.size()); //99
		Integer[] values = new Integer[tree.size()];
		tree.toArrays(values); // dump to a array
		for (Iterator<Integer>iter = tree.iterator(); iter.hasNext();) {
			int value = iter.next();
			if (value > 50)
				iter.remove();
		}
		for (int i : tree) {
			System.out.print(i + " ");
		}
		System.out.println();
	}
}
