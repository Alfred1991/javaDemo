package org.xiaofengcanyue.ranges;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.BoundType;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import org.checkerframework.checker.nullness.qual.Nullable;

public class AboutRanges {
    public static void main(String[] args) {

    }

    public static void buildRange(){
        Range.open(1,10);
        Range.closed(2.0f,9.1f);
        Range.greaterThan(10l);
        Range.atLeast(9d);
        Range.atMost(3.0);
        Range.all();
        Range.range(1,BoundType.CLOSED,10,BoundType.OPEN);
        Range.downTo(1,BoundType.CLOSED);
        Range.upTo(10,BoundType.CLOSED);
    }

    public static void operations(Range range){
        //query
        range.hasLowerBound();
        range.isEmpty();
        range.lowerEndpoint();

        //interval
        range.encloses(null);
        range.isConnected(null);
        range.intersection(null);
        range.span(null);

        //descrete domains
        ContiguousSet.create(range,DiscreteDomain.integers());
        range.canonical(DiscreteDomain.integers());

        /**
         * 1、A discrete domain always represents the entire set of values of its type;
         *   it cannot represent partial domains such as "prime integers" or "strings of length 5."
         *   So you cannot, for example, construct a DiscreteDomain to view a set of days in a range,
         *   with a JODA DateTime that includes times up to the second: because this would not contain all elements of the type.
         *
         * 2、A DiscreteDomain may be infinite -- a BigInteger DiscreteDomain, for example.
         *   In this case, you should use the default implementation of minValue() and maxValue(), which throw a NoSuchElementException.
         *   This forbids you from using the ContiguousSet.create method on an infinite range, however!
         */

    }
}
