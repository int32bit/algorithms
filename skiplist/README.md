# 跳表

跳表是一种随机化数据结构（其他随机化数据结构诸如treap、随机化快排），能够实现高概率的log(N)CURD操作，
效率和RB-tree AVL不相上下，是一种典型的空间换时间算法。

# Demo

```java
	  SkipList<Integer> list = new SkipList<>();
		for (int i = 0; i < 100; ++i) {
			list.add(100 - i);
		}
		for (int i : list) {
		  System.out.println(i);
		}
```
		
