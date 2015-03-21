#!/usr/bin/python
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
def main():
    result = sample("a.txt", 3)
    print(result)
main()
