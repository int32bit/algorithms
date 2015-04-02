# 小数转化成分数

如何把一个小数转化成分数，比如1.3, 转化成小数为13/10.为了简单起见，只考虑小数部分。

设X = 0.a<sub>1</sub>a<sub>2</sub>...a<sub>n</sub>(b<sub>1</sub>b<sub>2</sub>...b<sub>n</sub>)，
其中a<sub>1</sub>a<sub>2</sub>...a<sub>n</sub>为非循环部分，(b<sub>1</sub>b<sub>2</sub>...b<sub>n</sub>)为循环部分,比如0.3(3) = 1/3。

设A = a<sub>1</sub>a<sub>2</sub>...a<sub>n</sub>, B = b<sub>1</sub>b<sub>2</sub>...b<sub>n</sub>, 则
10<sup>n</sup>X = A + 0.(B), 即X = (A + 0.(B)) / 10<sup>n</sub>
考虑设 = 0.B, X = (A + Y) / 10<sup>n</sup>,即循环部分,10<sup>m</sub>Y = B + Y, 则Y = B / 10<sup>m</sup> - 1
则 X = (A * (10<sup>m</sup> - 1) + B) / (10<sup>n</sup> * (10<sup>m</sup - 1))
