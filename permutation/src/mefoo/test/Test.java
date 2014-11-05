package mefoo.test;
import java.util.*;
class Person {
    private String name;
    public Person(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
}
public class Test {
    public static void print(List<?> list) {
        for (int i = 0; i < list.size(); ++i)
            System.out.print(list.get(i) + " ");
        System.out.println();
    }
    public static <T> void print(T[] a) {
        for (int i = 0; i < a.length; ++i)
            System.out.print(a[i] + " ");
        System.out.println();
    }
    public static void main(String[] args) {
        //元素实现了Comparable, 使用列表
        List<Integer> list = Arrays.asList(1,2,3,4,5);
        print(list);
        while (Permutation.next(list)) {
            print(list);
        }
        // 元素实现了Comparable,使用数组
        Character [] ints = new Character[]{'A', 'B', 'C'};
        print(ints);
        while (Permutation.next(ints)) {
            print(ints);
        }
        //元素未实现Comparable, 需要放入桶里面，再调用next
        Person[] persons = new Person[]{new Person("Mary"), new Person("Jim"), new Person("Hery")};
        //Permutation.next(persons); // error
        Bucket<Person>[] buckets = Bucket.putIn(persons); //  put items into bucket before process
        print(buckets);
        while (Permutation.next(buckets)) { // that's Ok!
            print(buckets);
        }
    }
}

