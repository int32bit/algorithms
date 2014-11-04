What's heap ?
============

[堆(heap)](http://en.wikipedia.org/wiki/Heap_(data_structure))亦被称为：优先队列
(priority queue)，是计算机科学中一类数据结构统称。一般二叉树堆用的比较多，因此二叉树
堆也常常简称为堆，它是一棵完全二叉树，它的特点是父节点的值大于（小于）两个子节点的值
（分别称为大顶堆和小顶堆）。它常用于管理算法执行过程中的信息，应用场景包括堆排序，优
先队列等。

About My Simple Heap
====================

使用数组实现，参照java中java.util.PriorityQueue, 使用静态方法获取堆实例，可以获取
大顶堆、小顶堆，以及保持堆大小不变的filter堆，用于从大量数据中筛选排列在前K的对象。

How to use ?
============

1. 获取容量一定的过滤器
----------------------

```java
Heap<Integer> heap = Heap.getMaxFilter(10);
Random r = new Random();
Integer[] a = new Integer[10];
for (int i = 0; i < 100; ++i) {
	int value = r.nextInt(1000);
	heap.add(value);
}
//System.out.println(heap.size());
//System.out.println(heap.capacity());
heap.toArray(a);
for (Integer i : a) {
	System.out.print(i + " ");
}
```
2. 获取一个小根堆
----------------

```java
Heap<Integer> heap = Heap.getMinHeap();
Random r = new Random();
Integer[] a = new Integer[20];
for (int i = 0; i < 20; ++i) {
    int value = r.nextInt(100);
    heap.add(value);
}
heap.toArray(a);
for (Integer i : a) {
    System.out.print(i + " ");
}
```

