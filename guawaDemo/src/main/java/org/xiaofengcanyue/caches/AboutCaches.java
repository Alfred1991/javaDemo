package org.xiaofengcanyue.caches;

import com.google.common.cache.*;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class AboutCaches {

    /**
     * cache和concurrentMap类似，但cache会主动移除元素。
     */
    public static void createCaches(){

        /**
         * LoadingCache是由CacheLoader创建元素的Cache
         *
         * CacheLoader.asyncReloading()可以创建异步的CacheLoader
         */
        LoadingCache<String, Graph> graphs = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .removalListener(null)
                .build(
                        new CacheLoader<String, Graph>() {
                            @Override
                            public Graph load(String key) throws Exception {
                                return GraphBuilder.undirected().build();
                            }
                        });

        /**
         * 从callable创建元素
         *
         * 此外还能直接cache.put或者在Cache.asMap中进行put来插入数据
         */
        Cache<String,Graph> cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build();
        try{
            cache.get("", new Callable<Graph>() {
                @Override
                public Graph call() throws Exception {
                    return GraphBuilder.undirected().build();
                }
            });
        }catch (ExecutionException e){
            e.printStackTrace();
        }

    }

    /**
     * cache有三种evction type:size-based,time-based,reference-based
     */
    public static void aboutEviction(){

        /**
         * 指定weigher进行精确的size-based管理
         */
        LoadingCache<String, Graph> graphs = CacheBuilder.newBuilder()
                .maximumWeight(100000)
                .weigher(new Weigher<String, Graph>() {
                    public int weigh(String k, Graph g) {
                        return g.nodes().size();
                    }
                })
                .build(
                        new CacheLoader<String, Graph>() {
                            public Graph load(String key) { // no checked exception
                                return GraphBuilder.undirected().build();
                            }
                        });


        /**
         * CacheBuilder提供了expireAfterAccess和expireAfterWrite方法来进行time-based管理。
         *
         * CacheBuilder提供了weakKeys、weakValues和softValues方法来进行reference-based管理。
         * 使用weak时会导致cache使用==取代equals进行比较。
         * 使用soft时会导致cache使用==取代equals来比较values。
         *
         * 任何时刻可以使用invalidate*方法来清除cache entries。
         */

        /**
         * 可以在cache中执行removal listener，默认它是同步执行的。
         * 使用RemovalListeners.asynchronous(RemovalListener, Executor)可将其声明为异步执行的listener。
         */

        /**
         * caches不会自动cleanup，它写或读（当写很少时）操作时进行一些相关维护。
         * 如果写入操作较少，而又不希望在读取操作时会触发cleanup从而阻塞读取，此时可以开启一个线程周期性调用Cache.cleanUp()方法。
         */

        /**
         * cache提供refresh功能，返回旧值的同时创建一个新值（可异步）。
         * 通过重写CacheLoader.reload方法可以允许你在计算新值期间继续使用旧值。
         * CacheBuilder.refreshAfterWrite允许你周期性refresh整个cache。当某个entry处于refresh eligible状态时，只有查询它才会真正地触发refresh。
         * 可以对同一个cache同时指定refreshAfterWrite和expireAfterWrite。此时只有当某个entry eligible for refresh而又未被查询时，该entry才被允许expire。
         */
    }

    /**
     * 调用CacheBuilder.recordStats()可以开启cache的数据收集功能。
     * Cache.stats()方法返回一个CacheStats对象，该对象提供：
     *   1、hitRate()方法返回命中率。
     *   2、averageLoadPenalty()方法返回加载新值的平均时间，单位纳秒。
     *   3、evictionCount()方法返回cache evictions的数目。
     * 此外还有一些其他的指标，所有这些指标都有助于优化cache的使用。
     */

    /**
     * Cache.asMap可将其转化为一个ConcurrentMap。
     * Cache.asMap的返回值包含cache中当前所有的entries。
     * Cache.asMap().get(key)相当于cache.getIfPresent(key)，它不会触发load。
     * Cache.asMap上的操作不会重置cache的access time。
     */

}
