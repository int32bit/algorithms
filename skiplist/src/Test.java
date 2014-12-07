import java.util.Iterator;


public class Test {
	public static void main(String[] args) {
		SkipList<Integer> list = new SkipList<>();
		for (int i = 0; i < 1000; ++i)
			list.add(100 - i);
		for (int i : list)
			System.out.print("" + i + " ");
		System.out.println();
		for (Iterator<Integer> iter = list.reverseIterator(); iter.hasNext();) {
			int value = iter.next();
			if (value % 2 == 0)
				iter.remove();
			System.out.print("" + iter.next() + " ");
		}
		System.out.println();
		list.print();
	}
}
