package mefoo.util;
import java.util.*;
public class Permutation {
	private static <T extends Comparable<T>> int cmp(T e1, T e2) {
		return e1.compareTo(e2);
	}
    private static <T> void swap(List<T> list, int i, int j) {
        //assert(i < list.size() && j < list.size());
        if (i == j)
            return;
	list.set(i, list.set(j, l.get(i)));
    }
    private static <T> void swap(T[] array, int i, int j) {
        assert(i < array.length && j < array.length);
        if (i == j)
            return;
        T t = array[i];
        array[i] = array[j];
        array[j] = t;
    }
    private static <T> void reverse(List<T> list, int from, int to) {
        int i = from, j = to;
        while (i < j) {
            swap(list, i++, j--);
        }
    }
    private static <T> void reverse(T[] array, int from, int to) {
        int i = from, j = to;
        while (i < j) {
            swap(array, i++, j--);
        }
    }
    public static <T extends Comparable<T>> boolean next(T[] array) {
        int i, j;
		int len = array.length;
		for (i = len - 2; i >= 0; i--) {
			if (cmp(array[i], array[i + 1]) < 0)
                break;
		}
        if (i < 0) {
            reverse(array, 0, len - 1);
            return false;
        }
        for (j = len - 1; j > i; --j) {
            if (cmp(array[j], array[i]) > 0)
                break;
        }
        swap(array, i, j);
        reverse(array, i + 1, len - 1);
        return true;
    }
    public static <T extends Comparable<T>> boolean last(T[] array) {
        int i, j;
		int len = array.length;
		for (i = len - 2; i >= 0; i--) {
			if (cmp(array[i], array[i + 1]) > 0)
                break;
		}
        if (i < 0) {
            reverse(array, 0, len - 1);
            return false;
        }
        for (j = len - 1; j > i; --j) {
            if (cmp(array[j], array[i]) < 0)
                break;
        }
        swap(array, i, j);
        reverse(array, i + 1, len - 1);
        return true;
    }
	public static <T extends Comparable<T>> boolean next(List<T> list) {
        int i, j;
		int len = list.size();
		for (i = len - 2; i >= 0; i--) {
			if (cmp(list.get(i), list.get(i + 1)) < 0)
                break;
		}
        if (i < 0) {
            reverse(list, 0, len - 1);
            return false;
        }
        for (j = len - 1; j > i; --j) {
            if (cmp(list.get(j), list.get(i)) > 0)
                break;
        }
        swap(list, i, j);
        reverse(list, i + 1, len - 1);
        return true;
	}
	public static <T extends Comparable<T>> boolean last(List<T> list) {
        int i, j;
		int len = list.size();
		for (i = len - 2; i >= 0; i--) {
			if (cmp(list.get(i), list.get(i + 1)) > 0)
                break;
		}
        if (i < 0) {
            reverse(list, 0, len - 1);
            return false;
        }
        for (j = len - 1; j > i; --j) {
            if (cmp(list.get(j), list.get(i)) < 0)
                break;
        }
        swap(list, i, j);
        reverse(list, i + 1, len - 1);
        return true;
	}
}
