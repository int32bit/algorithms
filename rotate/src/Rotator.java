import java.util.*;
public class Rotator {
	private Rotator(){}
	private static final int RORATE_THRESHOLD = 100;
	public static void rotate(List<?> list, int distance) {
		if (list instanceof RandomAccess || list.size() < RORATE_THRESHOLD) {
			rorate1(list, distance);
		} else
			rorate2(list, distance);
	}
	private static <T> void rorate1(List<T> list, int distance) {
		int size = list.size();
		if (size == 0)
			return;
		distance %= size;
		if (distance < 0)
			distance += size;
		if (distance == 0)
			return;
		for (int cycleStart = 0, moved = 0; moved != size; cycleStart++) {
			T displaced = list.get(cycleStart);
			int i = cycleStart;
			do {
				i += distance;
				if (i >= size)
					i -= size;
				displaced = list.set(i, displaced);
				moved++;
			} while(i != cycleStart);
		}
	}
	private static <T> void rorate2(List<T> list, int distance) {
		int size = list.size();
		if (size == 0)
			return;
		int mid = -distance % size;
		if (mid < 0)
			mid += size;
		if (mid == 0)
			return;
		Collections.reverse(list.subList(0, mid));
		Collections.reverse(list.subList(mid, size));
		Collections.reverse(list);
	}
	public static <T> void rotate(T[]arr, int distance) {
		int size = arr.length;
		if (size == 0)
			return;
		distance %= size;
		if (distance < 0)
			distance += size;
		if (distance == 0)
			return;
		for (int cycleStart = 0, moved = 0; moved != size; cycleStart++) {
			T displaced = arr[cycleStart];
			int i = cycleStart;
			do {
				i += distance;
				if (i >= size)
					i -= size;
				T tmp = arr[i];
				arr[i] = displaced;
				displaced = tmp;
				moved++;
			} while(i != cycleStart);
		}
	}
	public static void forward(List<?> list, int index, int count, int distance) {
		int size = list.size();
		if (count <= 0 || count >= size)
			return;
		if (distance == 0)
			return;
		if (index < 0)
			index = 0;
		if (index + distance + count > size) {
			throw new IllegalArgumentException("index = " + distance + " distance = " + distance + " count = " + count);
		}
		if (count == size)
			return;
		rotate(list.subList(index, index + distance + count), -count); 
	}
	public static void forward(List<?> list, int index, int distance) {
		forward(list, index, 1, distance);
	}
	public static void backward(List<?> list, int index , int count, int distance) {
		int size = list.size();
		if (count <= 0 || count >= size)
			return;
		distance %= size;
		if (distance == 0)
			return;
		if (index >= size)
			index = size - 1;
		if (index - distance - count + 1 < 0)
			throw new IllegalArgumentException("index = " + distance + " distance = " + distance + " count = " + count);
		rotate(list.subList(index - distance - count + 1 , index + 1), count);
	}
	public static void backward(List<?> list, int index, int distance) {
		backward(list, index, 1, distance);
	}
	public static void print(Collection<?> arr) {
		for (Object o : arr)
			System.out.print(o + " ");
		System.out.println();
	}
	public static void print(Object[] arr) {
		for (Object o : arr)
			System.out.print(o + " ");
		System.out.println();
	}
	public static void main(String[] args) {
		Integer[] arr = new Integer[]{1,2,3,4,5};
		Rotator.rotate(arr, -2); // 将数组左移2位
		print(arr); // 3,4,5,1,2
		List<Integer> list = Arrays.asList(1,2,3,4,5);
		backward(list, 3, 1, 2); // 将列表索引3位置开始，长度为1,即[4],左移动2位
		print(list);// [1,4,2,3,5]
		list = Arrays.asList(1,2,3,4,5,6,7);
		forward(list, 1, 2, 3); // 将列表1开始长度为2,即[2,3], 右移动3位。
		print(list);// [1,4,5,6,2,3,7]
	}
}
