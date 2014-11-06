import java.util.*;
public class Shuffle {
	private static final Random random = new Random();
	private Shuffle() {

	}
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static void swap(List<?> list, int i, int j) {
		final List l = list;
		l.set(i, l.set(j, l.get(i)));
	}
	private static void swap(Object[] arr, int i, int j) {
		Object t = arr[i];
		arr[i] = arr[j];
		arr[j] = t;
	}
	public static <T> T[] shuffle(T[] arr, Random r) {
		int len = arr.length;
		for (int i = len; i > 0; --i) {
			swap(arr, i - 1, r.nextInt(i));
		}
		return arr;
	}
	public static <T> T[] shuffle(T[] arr) {
		return shuffle(arr, random);
	}
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static List<?> shuffle(List<?> list, Random r) {
		int len = list.size();
		if (list instanceof RandomAccess) {
			for (int i = len; i > 0; --i)
				swap(list, i - 1, r.nextInt(i));
			return list;
		}
		Object[] arr = list.toArray();
		shuffle(arr, r);
		ListIterator it = list.listIterator();
		for (int i = 0; i < arr.length; ++i) {
			it.next();
			it.set(arr[i]);
		}
		return list;
	}
	public static List<?> shuffle(List<?> list) {
		return shuffle(list, random);
	}
}
