BitSet
-----------

BitSet是用一个位来标识元素是否存在的位集，查找和插入、删除时间复杂度都是O(W),W是一个常数，表示一个word的长度，比如使用long，则W为64。并且元素原生按递增排序。

JAVA BitSet
----------

java.util.BitSet的变体，实现了java.util.Set接口.实现的功能为：
- 实现了java.util.Set接口，因此可以像使用HashSet一样，调用addAll, removeAll,iterator等方法。
- 和java.util.BitSet一样动态增加空间，当前使用空间取决于最大元素。
- 能够调用getMax(), getMin方法快速获取集合最值。
- 可以迭代，方便使用for循环进行遍历。
- 能够快速转化成int数组

注意
---------

只能添加大于等于0的整数，迭代时按递增遍历。

例子
--------

```java
// Creates a new BitSet
BitSet bitset = BitSet.valueOf(0,1,2,3,4,4, 128, 1, 127, 129);
bitset.addAll(Arrays.asList(99, 100, 102));
bitset.removeAll(Arrays.asList(1, 2, 3, 4));
assert(bitset.containsAll(Arrays.asList(127, 129, 128, 99, 100, 102)));
System.out.println("bitset: " + bitset);
System.out.println("max: " + bitset.getMax());
System.out.println("min: " + bitset.getMin());
System.out.println("size: " + bitset.size());
System.out.println("capacity: " + bitset.capacity());
System.out.print("iterator: [");
Iterator<?> iter = bitset.iterator();
while (iter.hasNext()) {
    System.out.print(iter.next() + ", ");
}
System.out.println("]");
```
