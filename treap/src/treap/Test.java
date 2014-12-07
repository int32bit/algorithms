package treap;

public class Test {
	public static void main(String[] args) {
			Treap<String> treap = new Treap<>();
			treap.add("G", 4);
			treap.add("B", 7);
			treap.add("A", 10);
			treap.add("E", 23);
			treap.add("H", 5);
			treap.add("K", 65);
			treap.add("I", 73);
			treap.remove("I");
			treap.remove("H");
			treap.remove("G");
			treap.add("C", 25);
			treap.add("D", 9);
			treap.add("F", 2);
			assert(treap.contains("A"));
			//treap.print();
			System.out.println("size : " + treap.size());
	}
}
