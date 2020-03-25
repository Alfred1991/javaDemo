package org.xiaofengcanyue.collections;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.*;
import sun.security.provider.certpath.Vertex;

import java.util.*;

public class NewCollectionTypes {

    public static void main(String[] args) {

    }

    /**
     * multiset是一个可以存在重复数据的set。
     * 有两种看待multiset的方式：
     *   1、类似于一个ArrayList<E>但没有顺序的限制。
     *   2、类似于一个Map<E,Integer>。
     * 当作为一个常规集合时，multiset类似于ArrayList；
     * 此外还提供类似Map<E,Integer>的额外操作:count,entrySet,elementSet等
     * multiset的内存消耗和元素distinct elements呈线性相关。
     */
    public static void aboutMultiSet(){
        Multiset<String> set = TreeMultiset.create();
        set = HashMultiset.create();
        set = LinkedHashMultiset.create();
        set = ConcurrentHashMultiset.create();
        set = ImmutableMultiset.of();
    }

    /**
     * multimap用于将一个key关联任意多个values。
     * 有两种看待multimap的方式：
     *   1、看做一个将存在重复的单个key映射到单个value的集合。
     *   2、看做一个将不存在重复的单个key映射到多个values的集合。
     * multimap接口一般呈现出第一种方式，asMap可让其以方式二呈现。
     * multimap中不建议包含value为null的键值对。
     */
    public static void aboutMultiMap(){
        ListMultimap<String,Integer> treeListMultimap = MultimapBuilder.treeKeys().arrayListValues().build();
        SetMultimap<Integer,MyEnum> hashMultimap = MultimapBuilder.hashKeys().enumSetValues(MyEnum.class).build();

        ArrayListMultimap.create();
        HashMultiset.create();
        LinkedListMultimap.create();
        LinkedHashMultiset.create();
        TreeMultimap.create();
        ImmutableListMultimap.of();
        ImmutableSetMultimap.of();

        Multimaps.<String,String>newMultimap(new HashMap<String, Collection<String>>(), Suppliers.ofInstance(new ArrayList<>()));
    }

    enum MyEnum {
        Type1,Type2;
    }

    /**
     * BiMap是一个Map，它允许：
     *   1、通过inverse()获得一个value到key的反向映射。
     *   2、确保values都是唯一的，将values作为一个set。
     * put重复值会触发IllegalArgumentException。BiMap.forcePut(key,value)可以强制覆盖重复的value。
     */
    public static void aboutBiMap(){
        HashBiMap.create();
        ImmutableBiMap.of();
        EnumBiMap.<MyEnum,MyEnum>create(MyEnum.class,MyEnum.class);
        EnumHashBiMap.create(MyEnum.class);
    }

    /**
     * table两个key的map，一个row key，一个column key。
     */
    public static void aboutTable(){
        Table<Vertex, Vertex, Double> weightedGraph = HashBasedTable.create();
        Vertex v1=null,v2=null,v3=null;
        weightedGraph.put(v1, v2, 4d);
        weightedGraph.put(v1, v3, 20d);
        weightedGraph.put(v2, v3, 5d);

        weightedGraph.row(v1); // returns a Map mapping v2 to 4, v3 to 20
        weightedGraph.column(v3); // returns a Map mapping v1 to 20, v2 to 5

        HashBasedTable.create();
        TreeBasedTable.create();
        ImmutableTable.of();
        ArrayTable.create(null);
    }

    /**
     * ClassToInstanceMap的值可以是多种类型的。
     * Technically, ClassToInstanceMap<B> implements Map<Class<? extends B>, B>
     */
    public static void aboutClassToInstanceMap(){
        ClassToInstanceMap<Number> numberDefaults = MutableClassToInstanceMap.create();
        numberDefaults.putInstance(Integer.class, Integer.valueOf(0));
    }

    /**
     * rangeset表示一个 disconnected,非空的 range 集合。
     * 当向其中添加range时，任何connected的ranges会合并，空的range会被忽略。
     *
     * Note that to merge ranges like Range.closed(1, 10) and Range.closedOpen(11, 15),
     * you must first preprocess ranges with Range.canonical(DiscreteDomain),
     * e.g. with DiscreteDomain.integers().
     */
    public static void aboutRangeSet(){
        RangeSet<Integer> rangeSet = TreeRangeSet.create();
        rangeSet.add(Range.closed(1, 10)); // {[1, 10]}
        rangeSet.add(Range.closedOpen(11, 15)); // disconnected range: {[1, 10], [11, 15)}
        rangeSet.add(Range.closedOpen(15, 20)); // connected range; {[1, 10], [11, 20)}
        rangeSet.add(Range.openClosed(0, 0)); // empty range; {[1, 10], [11, 20)}
        rangeSet.remove(Range.open(5, 10)); // splits [1, 10]; {[1, 5], [10, 10], [11, 20)}

        rangeSet.contains(10);
    }

    /**
     * rangemap 表示 disjoint,noempty的ranges 到 值 的映射的集合。
     * rangemap不会想rangeset那样合并range。
     */
    public static void aboutRangeMap(){
        RangeMap<Integer, String> rangeMap = TreeRangeMap.create();
        rangeMap.put(Range.closed(1, 10), "foo"); // {[1, 10] => "foo"}
        rangeMap.put(Range.open(3, 6), "bar"); // {[1, 3] => "foo", (3, 6) => "bar", [6, 10] => "foo"}
        rangeMap.put(Range.open(10, 20), "foo"); // {[1, 3] => "foo", (3, 6) => "bar", [6, 10] => "foo", (10, 20) => "foo"}
        rangeMap.remove(Range.closed(5, 11)); // {[1, 3] => "foo", (3, 5) => "bar", (11, 20) => "foo"}
    }

}
