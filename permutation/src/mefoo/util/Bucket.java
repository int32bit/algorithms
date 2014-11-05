package mefoo.util;
import java.util.*;
public class Bucket<T> implements Comparable<Bucket<T>> {
    private Integer id;
    private T value;
    private static int seq = 0;
    private Bucket(T value) {
        this.id = seq++;
        this.value = value;
    }
    public T get() {
        return value;
    }
    public int compareTo(Bucket<T> other) {
        return id.compareTo(other.id);
    }
    @Override
    public String toString() {
        return value.toString();
    }
    @SuppressWarnings("unchecked")
    public static <T> Bucket<T>[] putIn(T[] items) {
        Bucket<T>[] buckets = new Bucket[items.length];
        int i = 0;
        for (T e : items) {
            buckets[i++] = new Bucket(e);
        }
        return buckets;
    }
    @SuppressWarnings("unchecked")
    public static <T> Bucket<T>[] putIn(Collection<T> items) {
        Bucket<T>[] buckets = new Bucket[items.size()];
        int i = 0;
        for (T e : items) {
            buckets[i++] = new Bucket(e);
        }
        return buckets;
    }
}
