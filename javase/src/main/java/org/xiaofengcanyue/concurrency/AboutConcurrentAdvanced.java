package org.xiaofengcanyue.concurrency;

import javafx.print.Printer;
import sun.misc.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class AboutConcurrentAdvanced {

    /**
     * ReadWriteLock实际表示的是两个锁：读取操作使用的共享锁和写入操作使用的排他锁。
     *
     * ReentrantLock的优势在于减少了锁在各个线程之间的传递次数，提高程序的吞吐量。只在第一次获取锁是需要竞争，不用在每次获取锁的时都竞争。
     *
     */
    public class lockIdGenerator{
        private final ReentrantLock lock = new ReentrantLock();
        private int value = 0;
        public int getNext(){
            lock.lock();
            try{
                return value++;
            }finally {
                lock.unlock();
            }
        }
    }

    /**
     * Condition的使用需要一个对应的lock接口的实现对象。
     * Condition接口提供了多个类似Object类的wait方法的方法。
     * await对应wait，signal和signalAll对应notify和notifyAll。
     */
    public static void useCondition() throws InterruptedException {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        lock.lock();
        try{
            while (true){//条件不满足
                condition.await();
            }
        }finally {
            lock.unlock();
        }
    }

    /**
     * 如果程序中的同步方式可以抽象成有限个资源的同步访问，
     * 那么可以使用java.util.concurrent.locks包中的AbstractQueuedSynchronizer类和AbstractQueuedLongSynchronizer类作为实现的基础。
     */
    public static class SimpleResourceManager{
        private final InnerSynchronizer synchronizer;
        private static class InnerSynchronizer extends AbstractQueuedSynchronizer{
            InnerSynchronizer(int numOfResource){
                setState(numOfResource);
            }

            @Override
            protected int tryAcquireShared(int arg) {
                for(;;){
                    int available = getState();
                    int remaining = available - arg;
                    if(remaining < 0 || compareAndSetState(available,remaining)){
                        return remaining;
                    }
                }
            }

            @Override
            protected boolean tryReleaseShared(int arg) {
                for(;;){
                    int available = getState();
                    int next = available + arg;
                    if(compareAndSetState(available,next)){
                        return true;
                    }
                }
            }
        }

        public SimpleResourceManager(int numOfResources){
            synchronizer = new InnerSynchronizer(numOfResources);
        }

        public void acquire() throws InterruptedException{
            synchronizer.acquireSharedInterruptibly(1);
        }

        public void release(){
            synchronizer.releaseShared(1);
        }
    }

    /**
     * 信号量在操作系统中一般用来管理数量有限的资源。每类资源有一个对应的信号量，信号量的值表示资源的可用数量。
     * 在使用资源时，先从该信号量上获取一个使用许可。成功获取许可之后，资源的可用数减1.
     */
    public static class PrinterManager{
        private final Semaphore semaphore;
        private final List<Printer> printers = new ArrayList<>();
        public PrinterManager(Collection<? extends Printer> printers){
            this.printers.addAll(printers);
            this.semaphore = new Semaphore(this.printers.size(),true);
        }
        public Printer acquirePrinter() throws InterruptedException{
            semaphore.acquire();
            return getAvailablePrinter();
        }
        public void releasePrinter(Printer printer){
            putBackPrinter(printer);
            semaphore.release();
        }
        private synchronized Printer getAvailablePrinter(){
            Printer result = printers.get(0);
            printers.remove(0);
            return result;
        }
        private synchronized void putBackPrinter(Printer printer){
            printers.add(printer);
        }
    }

    /**
     * 倒数闸门：
     *  一个常见的情形是一个线程需要等待另外的线程完成某些任务之后才能继续运行，此时可以使用java.util.concurrent.CountDownLatch类。
     */
    public static class PageSizeSorter{
        private static final ConcurrentHashMap<String,Integer> sizeMap = new ConcurrentHashMap<>();
        private static class GetSizeWorker implements Runnable{
            private final String urlString;
            private final CountDownLatch signal;
            public GetSizeWorker(String urlString,CountDownLatch signal){
                this.urlString=urlString;
                this.signal=signal;
            }

            @Override
            public void run() {
                try {
                    InputStream is = new URL(urlString).openStream();
                    int size = IOUtils.readFully(is,-1,true).length;
                    sizeMap.put(urlString,size);
                } catch (IOException e) {
                    sizeMap.put(urlString,-1);
                }finally {
                    signal.countDown();
                }

            }
        }
        private void sort(){
            List<Map.Entry<String,Integer>> list = new ArrayList<>(sizeMap.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return Integer.compare(o2.getValue(),o1.getValue());
                }
            });
            System.out.println(Arrays.deepToString(list.toArray()));
        }
        public void sortPageSize(Collection<String> urls) throws InterruptedException{
            CountDownLatch sortSignal = new CountDownLatch(urls.size());
            for(String url : urls){
                new Thread(new GetSizeWorker(url,sortSignal)).start();
            }
            sortSignal.await();
            sort();
        }
    }

    /**
     * 循环屏障（java.util.concurrent.CyclicBarrier）类似于倒数闸门，但也有区别。
     * 首先循环屏障不像倒数闸门是一次性的，它可以循环使用。
     * 其次使用循环屏障的线程之间是平等的，彼此都需要等待对方完成。
     * 每个线程在完成任务之后，通过调用await方法进入等待状态，等所有线程都调用await方法之后，处于等待状态的线程都可以继续执行。
     * 在所有参与线程中，只要有一个在等待过程中被中断、出现超时或者其他错误，整个循环屏障会失效。所有处于等待状态的其他线程会抛出java.util.concurrent.BrokenBarrierException异常而结束。
     */
    public static class PrimeNumber{
        private static final int TOTAL_COUNT = 5000;
        private static final int RANGE_LENGTH = 200;
        private static final int WORKER_NUMBER = 5;
        private static volatile boolean done = false;
        private static int rangeCount = 0;
        private static final List<Long> results = new ArrayList<>();
        private static final CyclicBarrier barrier = new CyclicBarrier(WORKER_NUMBER, new Runnable() {
            @Override
            public void run() {
                if(results.size() >= TOTAL_COUNT){
                    done = true;
                }
            }
        });
        private static class PrimeFinder implements Runnable{
            @Override
            public void run() {
                while(!done){
                    int range = getNextRange();
                    long start = range * RANGE_LENGTH;
                    long end = (range + 1) * RANGE_LENGTH;
                    for(long i = start;i<end;i++){
                        if(isPrime(i)){
                            updateResult(i);
                        }
                    }
                    try {
                        barrier.await();
                    }catch (InterruptedException| BrokenBarrierException e){
                        done = true;
                    }
                }
            }
        }
        private synchronized static void updateResult(long value){
            results.add(value);
        }
        private synchronized static int getNextRange(){
            return rangeCount++;
        }
        private static boolean isPrime(long number){
            //此处省略
            return false;
        }
        public void calculate(){
            for(int i =0; i <WORKER_NUMBER;i++){
                new Thread(new PrimeFinder()).start();
            }
            while(!done){

            }
            //计算完成
        }
    }

    /**
     * 对象交换器适合于两个线程需要进行数据交换的场景。
     * 两个线程共享一个java.util.concurrent.Exchanger对象。
     * 一个线程完成对数据的处理之后，调用exchange方法把处理之后的数据作为参数发给另一个线程。
     * 而exchange方法的返回结果是另外一个线程所提供的相同类型的对象。
     * 如果另外一个线程尚未完成对数据的处理，那么exchange方法会使当前线程进入等待状态，直到另外一个线程也调用exchange方法来进行数据交换。
     */
    public static class SendAndReceiver {
        private final Exchanger<StringBuilder> exchanger = new Exchanger<StringBuilder>();
        private class Sender implements Runnable{
            @Override
            public void run() {
                try {
                    StringBuilder content = new StringBuilder("Hello");
                    content = exchanger.exchange(content);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        private class Receiver implements Runnable{
            @Override
            public void run() {
                try {
                    StringBuilder content = new StringBuilder("World");
                    content = exchanger.exchange(content);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        public void exchange(){
            new Thread(new Sender()).start();
            new Thread(new Receiver()).start();
        }
    }

    /**
     * 把Runnable、Future和Delayed接口组合起来，可以形成具备组合功能的新接口。
     *
     * Executor接口只能执行Runnable。ExecutorService接口继承自Executor接口，它提供了更多实用的功能并可执行Callable。
     *
     * 使用CompletionService接口可以获取已完成的Callable任务的Future返回值。
     *
     */
    public static class FileDownloader {
        private final ExecutorService executor = Executors.newFixedThreadPool(10);
        public boolean download(final URL url, final Path path){

            Future<Path> future = executor.submit(new Callable<Path>() {
                @Override
                public Path call() {
                    InputStream is = null;
                    try {
                        is = url.openStream();
                        Files.copy(is,path, StandardCopyOption.REPLACE_EXISTING);
                        return path;
                    } catch (IOException e) {
                        return null;
                    }
                }
            });
            try{
                return future.get() != null ? true : false;
            }catch (InterruptedException|ExecutionException e){
                return false;
            }
        }
        public void close(){
            executor.shutdown();
            try{
                if(!executor.awaitTermination(3,TimeUnit.MINUTES)){
                    executor.shutdownNow();
                    executor.awaitTermination(1,TimeUnit.MINUTES);
                }
            }catch (InterruptedException e){
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
