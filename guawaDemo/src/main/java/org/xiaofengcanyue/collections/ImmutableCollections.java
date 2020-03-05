package org.xiaofengcanyue.collections;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import java.awt.*;
import java.util.ArrayList;

/**
 * Interface	      JDK or Guava?	Immutable Version
 * Collection	            JDK	    ImmutableCollection
 * List	                    JDK	    ImmutableList
 * Set	                    JDK	    ImmutableSet
 * SortedSet/NavigableSet	JDK	    ImmutableSortedSet
 * Map	                    JDK	    ImmutableMap
 * SortedMap	            JDK	    ImmutableSortedMap
 * Multiset	                Guava	ImmutableMultiset
 * SortedMultiset	        Guava	ImmutableSortedMultiset
 * Multimap	                Guava	ImmutableMultimap
 * ListMultimap	            Guava	ImmutableListMultimap
 * SetMultimap	            Guava	ImmutableSetMultimap
 * BiMap	                Guava	ImmutableBiMap
 * ClassToInstanceMap	    Guava	ImmutableClassToInstanceMap
 * Table	                Guava	ImmutableTable
 *
 * 不可变对象的好处：
 *   1、对于untrusted libraries安全
 *   2、线程安全
 *   3、更节省内存
 *   4、可作为Constant
 * guava提供了所有的标准Collection类型的不可变版本在它的Colletion包中。
 * JDK也提供了Collections.unmodifiableXXX方法，但它存在以下不足：
 *   1、只有当original collection的引用没有被任何人持有时才是真正不可变的。
 *   2、效率低下，这个数据结构依然包含所有可变集合的overhead，包括concurrent modification checks,extra space in hash tables,etc。
 * guava中所有的不可变集合都不支持null值。
 */
public class ImmutableCollections {

    public static void main(String[] args) {
        ImmutableSet.builder().addAll(new ArrayList<>()).add("").build();

        ImmutableSet.of("a","b","c");

        ImmutableSet<String> foobar = ImmutableSet.of("foo","bar","baz");

        /**
         * ImmutableList.copyOf(foobar) will be smart enough to just return foobar.asList(), which is a constant-time view of the ImmutableSet.
         * ImmutableXXX.copyOf(ImmutableCollection)会避免linear-time copy：
         *   1、可以使用潜在的数据结构in constant time。例如ImmutableSet.copyOf(ImmutableList)就不能in constant time.
         *   2、它避免引起内存溢出。例如ImmutableList<String> hugeList，你需要做ImmutableList.copyOf(hugeList.subList(0,10))则会触发explicit copy。
         *   3、它并不改变语义。因此ImmutableSet.copyOf(myImmutableSortedSet)将触发explicit copy，因为ImmutableSet和ImmutableSortedSet的hashCode和equals是不同的。
         */
        ImmutableList<String> defensiveCopy = ImmutableList.copyOf(foobar);

    }


}
