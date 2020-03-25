package org.xiaofengcanyue.collections;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.*;
import com.google.common.primitives.Ints;

import java.util.*;

/**
 * Interface  JDK or Guava?	  Corresponding Guava utility class
 * Collection JDK	          Collections2
 * List	      JDK	          Lists
 * Set	      JDK	          Sets
 * SortedSet  JDK	          Sets
 * Map        JDK	          Maps
 * SortedMap  JDK	          Maps
 * Queue      JDK	          Queues
 * Multiset	  Guava	          Multisets
 * Multimap	  Guava	          Multimaps
 * BiMap      Guava	          Maps
 * Table      Guava	          Tables
 */
public class CollectionUtilities {

    public static void main(String[] args) {

    }

    public static void aboutStaticConstructors(){
        List<String> list = Lists.newArrayList();
        Map<String,String> map = Maps.newLinkedHashMap();

        Set<String> copySet = Sets.newHashSet("1","2");
        List<String> theseElements = Lists.newArrayList("alpha","beta","gamma");

        List<String> exactly100 = Lists.newArrayListWithCapacity(100);
        List<String> approx100 = Lists.newArrayListWithExpectedSize(100);
        Set<String> approx100Set = Sets.newHashSetWithExpectedSize(100);

        Multiset<String> multiset = HashMultiset.create();
    }

    /**
     * 相比于Collection，更推荐使用Iterable。
     * Iterables类的多数操作都是lazy的。
     * 从Guava12以后，Iterables被FluentIterable所补充,FluentIterable包装了一个Iterable并提供一些"fluent"方式的操作。
     */
    public static void aboutIterables(){
        Iterable<Integer> concatenated = Iterables.concat(
                Ints.asList(1, 2, 3),
                Ints.asList(4, 5, 6));
// concatenated has elements 1, 2, 3, 4, 5, 6

        Integer lastAdded = Iterables.getLast(concatenated);

        Integer theElement = Iterables.getOnlyElement(concatenated);
        // if this set isn't a singleton, something is wrong!

    }

    public static void aboutLists(){
        List<Integer> countUp = Ints.asList(1, 2, 3, 4, 5);
        List<Integer> countDown = Lists.reverse(countUp); // {5, 4, 3, 2, 1}

        List<List<Integer>> parts = Lists.partition(countUp, 2); // {{1, 2}, {3, 4}, {5}}
    }

    public static void aboutSets(){
        Set<String> wordsWithPrimeLength = ImmutableSet.of("one", "two", "three", "six", "seven", "eight");
        Set<String> primes = ImmutableSet.of("two", "three", "five", "seven");

        Sets.SetView<String> intersection = Sets.intersection(primes, wordsWithPrimeLength); // contains "two", "three", "seven"
// I can use intersection as a Set directly, but copying it can be more efficient if I use it a lot.
        intersection.immutableCopy();

        Set<String> animals = ImmutableSet.of("gerbil", "hamster");
        Set<String> fruits = ImmutableSet.of("apple", "orange", "banana");

        Set<List<String>> product = Sets.cartesianProduct(animals, fruits);
// {{"gerbil", "apple"}, {"gerbil", "orange"}, {"gerbil", "banana"},
//  {"hamster", "apple"}, {"hamster", "orange"}, {"hamster", "banana"}}

        Set<Set<String>> animalSets = Sets.powerSet(animals);
// {{}, {"gerbil"}, {"hamster"}, {"gerbil", "hamster"}}
    }

    public static void aboutMaps(){
        ImmutableMap<Integer, String> stringsByIndex = Maps.uniqueIndex(new ArrayList<String>(), new Function<String, Integer>() {
            public Integer apply(String string) {
                return string.length();
            }
        });

        Map<String, Integer> left = ImmutableMap.of("a", 1, "b", 2, "c", 3);
        Map<String, Integer> right = ImmutableMap.of("b", 2, "c", 4, "d", 5);
        MapDifference<String, Integer> diff = Maps.difference(left, right);

        diff.entriesInCommon(); // {"b" => 2}
        diff.entriesDiffering(); // {"c" => (3, 4)}
        diff.entriesOnlyOnLeft(); // {"a" => 1}
        diff.entriesOnlyOnRight(); // {"d" => 5}
    }

    /**
     * The Guava utilities on BiMap live in the Maps class, since a BiMap is also a Map.
     */
    public static void aboutBiMap(){
        Maps.synchronizedBiMap(HashBiMap.create());
        Maps.unmodifiableBiMap(HashBiMap.create());
    }

    public static void aboutMultiSets(){
        Multiset<String> multiset1 = HashMultiset.create();
        multiset1.add("a", 2);

        Multiset<String> multiset2 = HashMultiset.create();
        multiset2.add("a", 5);

        multiset1.containsAll(multiset2); // returns true: all unique elements are contained,
        // even though multiset1.count("a") == 2 < multiset2.count("a") == 5
        Multisets.containsOccurrences(multiset1, multiset2); // returns false

        Multisets.removeOccurrences(multiset1,multiset2); // multiset2 now contains 3 occurrences of "a"

        multiset2.removeAll(multiset1); // removes all occurrences of "a" from multiset2, even though multiset1.count("a") == 2
        multiset2.isEmpty(); // returns true

        Multiset<String> multiset = HashMultiset.create();
        multiset.add("a", 3);
        multiset.add("b", 5);
        multiset.add("c", 1);

        ImmutableMultiset<String> highestCountFirst = Multisets.copyHighestCountFirst(multiset);

        Multisets.unmodifiableMultiset(multiset);
        Multisets.unmodifiableSortedMultiset(TreeMultiset.create());
    }

    public static void aboutMultimaps(){
        ImmutableSet<String> digits = ImmutableSet.of(
                "zero", "one", "two", "three", "four",
                "five", "six", "seven", "eight", "nine");
        Function<String, Integer> lengthFunction = new Function<String, Integer>() {
            public Integer apply(String string) {
                return string.length();
            }
        };
        ImmutableListMultimap<Integer, String> digitsByLength = Multimaps.index(digits, lengthFunction);
        /*
         * digitsByLength maps:
         *  3 => {"one", "two", "six"}
         *  4 => {"zero", "four", "five", "nine"}
         *  5 => {"three", "seven", "eight"}
         */


        ArrayListMultimap<String, Integer> multimap = ArrayListMultimap.create();
        multimap.putAll("b", Ints.asList(2, 4, 6));
        multimap.putAll("a", Ints.asList(4, 2, 1));
        multimap.putAll("c", Ints.asList(2, 5, 3));

        TreeMultimap<Integer, String> inverse = Multimaps.invertFrom(multimap, TreeMultimap.<Integer,String>create());
        // note that we choose the implementation, so if we use a TreeMultimap, we get results in order
        /*
         * inverse maps:
         *  1 => {"a"}
         *  2 => {"a", "b", "c"}
         *  3 => {"c"}
         *  4 => {"a", "b"}
         *  5 => {"c"}
         *  6 => {"b"}
         */

        Map<String, Integer> map = ImmutableMap.of("a", 1, "b", 1, "c", 2);
        SetMultimap<String, Integer> multimap1 = Multimaps.forMap(map);
        // multimap maps ["a" => {1}, "b" => {1}, "c" => {2}]
        Multimap<Integer, String> inverse1 = Multimaps.invertFrom(multimap1, HashMultimap.<Integer, String> create());
        // inverse maps [1 => {"a", "b"}, 2 => {"c"}]


        ListMultimap<String, Integer> myMultimap = Multimaps.newListMultimap(
                Maps.<String, Collection<Integer>>newTreeMap(),
                new Supplier<LinkedList<Integer>>() {
                    public LinkedList<Integer> get() {
                        return Lists.newLinkedList();
                    }
                });
    }

    public static void aboutTables(){
        // use LinkedHashMaps instead of HashMaps
        Table<String, Character, Integer> table = Tables.newCustomTable(
                Maps.<String, Map<Character, Integer>>newLinkedHashMap(),
                new Supplier<Map<Character, Integer>> () {
                    public Map<Character, Integer> get() {
                        return Maps.newLinkedHashMap();
                    }
                });

        Tables.transpose(table);

        Tables.unmodifiableTable(table);
    }

}
