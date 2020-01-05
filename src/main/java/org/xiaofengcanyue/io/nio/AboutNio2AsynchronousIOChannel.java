package org.xiaofengcanyue.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AboutNio2AsynchronousIOChannel {

    public static void main(String[] args) {

    }

    /**
     * 使用future接收异步io的结果
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void asyncWrite() throws IOException, ExecutionException,InterruptedException {
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get("large.bin"), StandardOpenOption.CREATE,StandardOpenOption.WRITE);
        ByteBuffer buffer = ByteBuffer.allocate(32 * 1024 * 2014);
        /**
         异步io中没有当前位置的概念，因此需要指定起始位置。
         */
        Future<Integer> result = channel.write(buffer,0);
        Integer len = result.get();
    }

    public static void startAsyncSimpleServer() throws IOException{
        AsynchronousChannelGroup group = AsynchronousChannelGroup.withFixedThreadPool(10, Executors.defaultThreadFactory());

        /**
         此处若不指定channelgroup，则会使用系统默认的守护线程。因此会很快退出
         */
        final AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open(group).bind(new InetSocketAddress(10080));
        serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel result, Void attachment) {

            }

            @Override
            public void failed(Throwable exc, Void attachment) {

            }
        });
    }
}
