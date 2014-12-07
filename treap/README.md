# 树堆

Treap是一种随机化数据结构，满足堆的性质的二叉搜索树，其结构相当于以随机顺序插入的BST，用来克服一般的BST由于输入有序时导致
退化成线性。其基本操作的期望复杂度为O(log n)。其特点是实现简单，效率高于伸展树并且支持大部分基本功能，性价比很高。
Treap虽然不能完全保证是平衡的，但很大概率是接近平衡的，它比标准的AVL开销小，而又比一般的BST效率高，因此是二者的折中。

## 实现

其实Treap是BST的扩展，可以看作是BST + heap .节点信息比BST多保存了一个额外数据，即优先级。Treap在以关键码构成二叉搜索树的同时，
还按优先级来满足堆的性质。

## 查找
和BST算法一样，平均复杂度O(logn).

## 插入（假设小根堆， 插入新节点为p）

1. 先按BST插入算法，把节点插入到叶子上。
2. 若p不是root 并且p的优先级小于parent的优先级，则转3. 否则转4.
3. 如果p是左孩子，则右旋转parent节点。若p是右孩子，左旋转parent记得。转2.
4. 结束。

```java
void heapify(Node<E> p) {
		while (p != root) {
			Node<E> parent = p.parent;
			if (p.getPriority() < parent.getPriority()) {
				if (p == parent.left)
					rightRotate(parent);
				else
					leftRotate(parent);
			} else {
				return;
			}
		}
	}
```

## 删除
	
	1. 找到删除的元素，设为p，若p不存在，算法结束。
	2. 若p是叶子节点，直接删除，算法结束，否则转3.
	3. 从p的孩子节点中找到优先级低的节点c（单孩子则返回唯一的孩子节点）。
	4. 若c是p的左孩子，则右旋转p，否则左旋转p，转2.
	
```java
	public boolean remove (E e) {
		Node<E> p = find(e);
		if (p == null)
			return false;
		while (!isLeaf(p)) {
			Node<E> candicate = getMinChild(p);
			if (candicate == p.left)
				rightRotate(p);
			else
				leftRotate(p);
		}
		removeFromLeaf(p);
		++modCount;
		--size;
		return true;
	}
```

## Demo
```java
Treap<String> treap = new Treap<>();
			treap.add("G", 4);
			treap.add("B", 7);
			treap.add("A", 10);
			treap.add("E", 23);
			treap.add("H", 5);
			treap.add("K", 65);
			treap.add("I", 73);
			treap.remove("I");
			treap.remove("H");
			treap.remove("G");
			treap.add("C", 25);
			treap.add("D", 9);
			treap.add("F", 2);
			assert(treap.contains("A"));
			//treap.print();
			System.out.println("size : " + treap.size());
```
