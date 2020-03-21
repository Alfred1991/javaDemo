package org.xiaofengcanyue.collections;

import com.google.common.collect.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CollectionExtensionHelpers {

    /**
     * Forwarding类的delegate()方法将会返回被装饰的对象。
     *
     * Interface	  Forwarding Decorator
     * Collection	  ForwardingCollection
     * List	          ForwardingList
     * Set	          ForwardingSet
     * SortedSet	  ForwardingSortedSet
     * Map	          ForwardingMap
     * SortedMap	  ForwardingSortedMap
     * ConcurrentMap  ForwardingConcurrentMap
     * Map.Entry	  ForwardingMapEntry
     * Queue	      ForwardingQueue
     * Iterator	      ForwardingIterator
     * ListIterator	  ForwardingListIterator
     * Multiset	      ForwardingMultiset
     * Multimap	      ForwardingMultimap
     * ListMultimap	  ForwardingListMultimap
     * SetMultimap	  ForwardingSetMultimap
     */
    public static class AboutForwardingDecorators{

        /**
         * by default, all methods forward directly to the delegate, so overriding ForwardingMap.put will not change the behavior of ForwardingMap.putAll.
         * @param <E>
         */
        class AddLoggingList<E> extends ForwardingList<E> {
            AddLoggingList(List<E> delegate){
                this.delegate = delegate;
            }
            final List<E> delegate; // backing list
            @Override protected List<E> delegate() {
                return delegate;
            }
            @Override public void add(int index, E elem) {
                //log(index, elem);
                super.add(index, elem);
            }
            @Override public boolean add(E elem) {
                return standardAdd(elem); // implements in terms of add(int, E)
            }
            @Override public boolean addAll(Collection<? extends E> c) {
                return standardAddAll(c); // implements in terms of add
            }
        }

    }

    /**
     * Note: the PeekingIterator returned by Iterators.peekingIterator does not support remove() calls after a peek()
     */
    public static void AboutPeekingIterator(){
        List<String> source = Lists.newArrayList();
        List<String> result = Lists.newArrayList();
        PeekingIterator<String> iter = Iterators.peekingIterator(source.iterator());
        while (iter.hasNext()) {
            String current = iter.next();
            while (iter.hasNext() && iter.peek().equals(current)) {
                // skip this duplicate element
                iter.next();
            }
            result.add(current);
        }
    }

    /**
     * About AbstractIterator:
     *   You implement one method, computeNext(), that just computes the next value.
     *   When the sequence is done, just return endOfData() to mark the end of the iteration.
     */
    public static Iterator<String> skipNulls(final Iterator<String> in) {
        return new AbstractIterator<String>() {
            protected String computeNext() {
                while (in.hasNext()) {
                    String s = in.next();
                    if (s != null) {
                        return s;
                    }
                }
                return endOfData();
            }
        };
    }

    /**
     * Note that you must additionally pass an initial value, or null if the iterator should end immediately.
     */
    public static void AboutAbstractSequenceIterator(){
        Iterator<Integer> powersOfTwo = new AbstractSequentialIterator<Integer>(1) { // note the initial value!
            /**
             * Here, we implement the method computeNext(T), which accepts the previous value as an argument.
             */
            protected Integer computeNext(Integer previous) {
                return (previous == 1 << 30) ? null : previous * 2;
            }
        };
    }
}
