package org.xiaofengcanyue.basicutilities;

import com.google.common.base.Function;
import com.google.common.primitives.Ints;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * "fluent" Comparator class,which can be used to build complex comparators and apply them to collections of objects.
 * 在其核心，一个Ordering实际是一个特殊的Comparator实例。
 * Ordering将 依赖于Comparator的方法(例如Collections.max) 变为 实例方法。
 * 此外Ordering 提供了 chaining methods 来 tweak，并强化了 existing comparators。
 */
public class Ordering {

    public static void main(String[] args) {

    }

    public static void Creation(){
        com.google.common.collect.Ordering.natural();
        com.google.common.collect.Ordering.usingToString();
        com.google.common.collect.Ordering.from(new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
                return 0;
            }
        });
        //更一般的创建方式
        com.google.common.collect.Ordering<String> byLengthOrdering = new com.google.common.collect.Ordering<String>(){
            @Override
            public int compare(@Nullable String left, @Nullable String right) {
                return Ints.compare(left.length(),right.length());
            }
        };
    }

    public static void Chaining(com.google.common.collect.Ordering order){
        order.reverse();
        order.nullsFirst();
        order.compound(new Comparator() {
            public int compare(Object o1, Object o2) {
                return 0;
            }
        });
        order.lexicographical();
        order.onResultOf(new Function() {
            @Nullable
            public Object apply(@Nullable Object input) {
                return null;
            }
        });
    }

    private static class Foo {
        @Nullable String sortedBy;
        int notSortedBy;
    }

    /**
     * When reading a chain of Ordering calls, work "backward" from right to left.
     * The example below orders Foo instances by looking up their sortedBy field values,
     * first moving any null sortedBy values to the top
     * and then sorting the remaining values by natural string ordering.
     * @return
     */
    public static com.google.common.collect.Ordering Example(){
        com.google.common.collect.Ordering<Foo> ordering
                = com.google.common.collect.Ordering.
                natural().
                nullsFirst().
                onResultOf(new Function<Foo, String>() {
                    public String apply(Foo foo) {
                        return foo.sortedBy;
                    }
                });
        return ordering;
    }

    public static void Application(com.google.common.collect.Ordering order){
        order.greatestOf(new ArrayList(),1);
        order.isOrdered(new ArrayList());
        order.sortedCopy(new ArrayList());
        order.min("","");
    }


}
