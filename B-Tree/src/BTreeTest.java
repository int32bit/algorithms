import java.util.Iterator;
import java.util.*;


public class BTreeTest {
	public static void main(String[] args) {
		BTree<Integer> tree = new BTree<Integer>(3);
		for (int i = 1; i <= 100; i++) {
			tree.add(i);
		}
		tree.remove(50);
		for (Iterator<Integer> iter = tree.iterator(); iter.hasNext();) {
			int value = iter.next();
			if (value > 50 || (value & 1) == 0) {
				iter.remove();
				//System.out.println(value + " removed");
			}
		}
		System.out.println("height: " + tree.getHeight());
		System.out.println("size: " + tree.size());
		for (int i : tree) {
			System.out.print(i + " ");
		}
		System.out.println();
	}
}
