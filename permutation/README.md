排列问题
=======

n个元素按照一定顺序排成一列，叫做这n个元素的一个排列。n个元素共有n！种排列。如果元素是可比较的，则这n个元素各个排列之间也是可比较的，即平常说的字典序。


假设n个元素是可比较的，如何从小到大输出各排列？或者说给定一个排列，如何求出下一个排列？

学习了July的[字符串的全排列](https://github.com/julycoding/The-Art-Of-Programming-By-July/blob/master/ebook/zh/01.06.md),于是自己用java实现了下。

next-permutation java实现
========================

调用Permutation的静态next方法即可生成下一个排列，传入参数可以是实现*Comparable*的List或者数组，如果参数没有实现*Comparable*，则必须先放入*Bucket*桶中，再处理！

Demo
====
```java
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
```


