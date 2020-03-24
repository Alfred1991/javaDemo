package org.xiaofengcanyue.concurrency;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.*;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * ListenableFuture扩展了Future，它允许注册回调函数在Future计算完成后执行。
 */
public class AboutListenableFuture {

    public static void AboutCreationg(){
        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
        ListenableFuture<Object> explosion = service.submit(
                new Callable<Object>() {
                    public Object call() {
                        return null;
                    }
                });
        Futures.addCallback(
                explosion,
                new FutureCallback<Object>() {
                    // we want this handler to run immediately after we push the big red button!
                    public void onSuccess(Object explosion) {

                    }
                    public void onFailure(Throwable thrown) {

                    }
                },
                service);


        /**
         * 若使用FutureTask
         */
        ListenableFutureTask.create(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return null;
            }
        });

        ListenableFutureTask.create(new Runnable(){
            @Override
            public void run() {

            }
        },null);

        /**
         * 若实现一个抽象类型以便设置future的值
         */
        new AbstractFuture<Object>(){
        };
        SettableFuture.create().setFuture(null);

        /**
         * 若转化一个Future
         */
        JdkFutureAdapters.listenInPoolThread(null);
    }

    /**
     * ListenableFuture常用于异步的处理链
     */
    public static void AboutApplication(){
        /**

        ListenableFuture<RowKey> rowKeyFuture = indexService.lookUp(query);
        AsyncFunction<RowKey, QueryResult> queryFunction =
                new AsyncFunction<RowKey, QueryResult>() {
                    public ListenableFuture<QueryResult> apply(RowKey rowKey) {
                        return dataService.read(rowKey);
                    }
                };
        ListenableFuture<QueryResult> queryFuture =
                Futures.transformAsync(rowKeyFuture, queryFunction, queryExecutor);

         */
        Futures.transform(null,null,null);
        Futures.transformAsync(null,null,null);
        Futures.allAsList(Lists.newArrayList());
        Futures.successfulAsList(Lists.newArrayList());
    }
}
