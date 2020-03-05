package org.xiaofengcanyue.collections;

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public static void AboutStaticConstructors(){
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
    public static void AboutIterables(){
        Iterable<Integer> concatenated = Iterables.concat(
                Ints.asList(1, 2, 3),
                Ints.asList(4, 5, 6));
// concatenated has elements 1, 2, 3, 4, 5, 6

        Integer lastAdded = Iterables.getLast(concatenated);

        Integer theElement = Iterables.getOnlyElement(concatenated);
        // if this set isn't a singleton, something is wrong!

    }

    public static void AboutLists(){
        List<Integer> countUp = Ints.asList(1, 2, 3, 4, 5);
        List<Integer> countDown = Lists.reverse(countUp); // {5, 4, 3, 2, 1}

        List<List<Integer>> parts = Lists.partition(countUp, 2); // {{1, 2}, {3, 4}, {5}}
    }

    public static void AboutSets(){
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

    public static void AboutMaps(){
        ImmutableMap<Integer, String> stringsByIndex = Maps.uniqueIndex(new ArrayList<String>(), new Function<String, Integer>() {
            public Integer apply(String string) {
                return string.length();
            }
        });
    }

}
