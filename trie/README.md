Trie
-----
In computer science, a trie, also called digital tree and sometimes
radix tree or prefix tree (as they can be searched by prefixes), is
an ordered tree data structure that is used to store a dynamic set
or associative array where the keys are usually strings.
See [Wikipedia](http://en.wikipedia.org/wiki/Trie)

My Simple Trie
=================================

Just for fun!

Demo
--------

```java
    Trie trie = new Trie(20);
    trie.addAll(Arrays.asList("one", "two", "three"));
    trie.add("one");
    assert(trie.contains("one"));
    assert(trie.size() == 5);
    assert(trie.count("one") == 2);
    trie.remove("one");
    assert(!trie.contains("one"));
    for (String word : trie.toArray()) {
        System.out.println(word);
    }
    trie.clear();
    assert(trie.isEmpty());
```
