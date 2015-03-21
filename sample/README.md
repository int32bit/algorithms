# 蓄水池抽样

什么是蓄水池抽样，它能解决什么问题?
## 从一次面试说起

百度面试以算法为主啊，手动写代码。第一道题是实现c语言库函数strcpy，这个原理很简单，但要注意以下这几点：
* 空指针检查（包括src和dest）
* 内存重叠，要检查指针是否重叠
* 最后拷贝时，别忘了在dest追加字符串终结符号0
* 如何保证dest已分配足够内存

第二道题是写一个类，实现堆的操作。说实话，虽然堆的操作不难，但要真正实现它并不容易。以下需要注意的点：
* 泛型参数（泛化编程）
* 类型必需可比较或者提供比较器
* 不能实例泛型类型，即不能new T[]或者new T()，只能使用Object数组
* 内存动态分配，内存拷贝（数组拷贝）

重点是第三道题目：给定一个文件，从中随机取出n行，并计算时间复杂度。我上来便是这样的代码：
```python
import random
def sample(filename, n = 3):
    assert(filename is not None)
    result = []
    with open(filename) as f:
        lines = f.readlines()
    while len(result) < n :
        line = random.choice(lines)
        if line not in result:
            result.append(line.strip())
    return result
```
这种方法
* 必须把文件内容全读入内存，如果文件很大怎么办？
* 设N为文件行数，n和N接近时，越到后面，冲突越大，效率极低。
* 
有人说，先随机生成n个数构成一个集合A（还是有冲突哦），读取文件，当行数属于A时，取出该行，问题不就解决了？
问题是文件总行数是多少？必须知道文件总行数才能知道随机数的取值范围啊。必须先扫描一遍文件？文件很大时这样的效率如何？

后来面试官问我知不知道分治法，我说了解，但这有关系么？文件分割建索引？全错！！！

## 蓄水池抽样

>> 蓄水池抽样（Reservoir Sampling ）是一个很有趣的问题，它能够在o（n）时间内对n个数据进行等概率随机抽取，
>> 例如：从1000个数据中等概率随机抽取出100个。另外，如果数据集合的量特别大或者还在增长（相当于未知数据集合总量），
>> 该算法依然可以等概率抽样。

以上摘自[handspeaker博客](http://www.cnblogs.com/hrlnw/archive/2012/11/27/2777337.html)

算法步骤为：

1. 从文件中取前n行，结果集result
2. 令i表示当前行数，c为当前行内容，m = random(1, i)，即m为1～i的一个随机数。
3. 若m <= n, 则令result[m] = c，即替换第m行内容，否则舍弃c。
4. 若已到文件结尾，退出算法，返回result，否则转2

利用归纳法可以证明每行被取的概率是相等的，证明过程
见[handspeaker博客](http://www.cnblogs.com/hrlnw/archive/2012/11/27/2777337.html)

## 回到问题
问题解决了，毫无悬念！
```python
import random
def sample(filename, n = 5):
    assert(filename is not None)
    result = []
    with open(filename, mode = "r") as f:
        for i in range(n):
            line = f.readline()
            if line == '':
                return result
            else:
                result.append(line.strip())
        i = n
        for line in f:
            k = random.randint(0, i)
            if k < n:
                result[k] = line.strip()
            i += 1
    return result
```
