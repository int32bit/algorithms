#!/usr/bin/python3
#coding=utf-8
import sys
class Util:
    @staticmethod
    def isEven(a):
        a = int(a)
        if (a & 0x1) == 1:
            return False
        else:
            return True
    @staticmethod
    def gcd(a, b):
        if a < b:
            return Util.gcd(b, a)
        if b == 0:
            return a
        if Util.isEven(a):
            if Util.isEven(b):
                return Util.gcd(a >> 1, b >> 1) << 1
            else:
                return Util.gcd(a >> 1, b)
        else:
            if Util.isEven(b):
                return Util.gcd(a, b >> 1)
            else:
                return Util.gcd(b, a - b)
class Fraction:
    def __init__(self, sign = 0, integer = 0, numerator = 0,denominator = 1):
        i = int(integer)
        self.__sign__ = sign
        if i < 0:
            self.__sign__ = 1
            i = -i
        m = int(denominator)
        f = int(numerator)
        ff = f + i * m
        mm = m
        common_divisor = Util.gcd(ff, mm)
        self.__numerator__ = ff // common_divisor
        self.__denominator__ = mm // common_divisor
        if self.__denominator__ == 1:
            self.__isInteger__ = True
        else:
            self.__isInteger__ = False
    def isZero(self):
        return self.__numerator__ == 0
    def __str__(self):
        if self.isZero():
            return '0'
        sign = "-" if self.__sign__ == 1 else ''
        if self.__isInteger__:
            return "%s%s" %(sign, self.__numerator__)
        else:
            return "%s%s/%s" %(sign, self.__numerator__, self.__denominator__)
    @staticmethod
    def parse(s):
        # 空字符串返回0
        if s is None or len(s.strip()) == 0:
            return Fraction(0)
        s = s.strip()
        parts = s.split('.')
        integerPart = parts[0]
        sign = 0
        # 整数部分，可能为负数
        if len(integerPart) > 0:
            assert integerPart.isdigit() or (integerPart[0] == '-' and integerPart[1:].isdigit())
            sign = 1 if integerPart.startswith('-') else 0
        else:
            integerPart = '0'
        # 只有整数部分
        if len(parts) < 2 or len(parts[1]) < 1:
            return Fraction(sign, integerPart)
        pointPart = parts[1]
        # 解析小数部分
        f, m = Fraction.parsePoint(pointPart)
        return Fraction(sign, integerPart, f, m)
    @staticmethod
    def parsePoint(s):
        if s is None or len(s.strip()) == 0:
            return 0, 1
        parts = s.split('(')
        nonLoop = parts[0]
        if len(nonLoop) > 0:
            assert nonLoop.isdigit(), "%s cann't be converted to digit" % nonLoop
        # 有循环部分
        if len(parts) > 1:
            loop = parts[1]
            assert(loop[-1] == ')')
            loop = loop[:-1]
            assert(loop.isdigit())
        else:
            # 没有循环部分
            return int(nonLoop), 10 ** len(nonLoop)
        # 只有循环部分
        if len(nonLoop) == 0:
            return int(loop), 10 ** len(loop) - 1
        lm = 10 ** len(loop) - 1
        f = int(nonLoop) * lm + int(loop)
        m = (10 ** len(nonLoop)) * lm
        return f, m
def main(*args, **kwargs):
    while True:
        try:
            i = input()
        except EOFError:
            print("quit")
            break
        except KeyboardInterrupt:
            print("killed")
            sys.exit(1)
        if i == "quit" or i == "exit":
            break
        print(Fraction.parse(i))
main(sys.argv[1:])
