
public class BTreeTest {
	public static void main(String[] args) {
		BTree<Integer> tree = new BTree<Integer>(5);
		for (int i = 0; i < 100; i++) {
			tree.add(i);
		}
		for (int i : tree){
			System.out.println(i);
		}
	}
}
