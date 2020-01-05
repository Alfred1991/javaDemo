package org.xiaofengcanyue.concurrent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class AboutConcurrentBasics {

    /**
     * 静态方法对应的监视器对象是所在java类对象的Class累的对象所关联的监视器对象，
     * 实例方法使用的是当前对象实例所关联的监视器对象，
     * synchronized代码块使用的是代码块声明中的对象所关联的监视器对象。
     *
     * java虚拟机和编译器负责完成实际的同步工作。
     * 当锁被释放时，对共享变量的修改会从CPU缓存中直接写回到主存中；
     * 当锁被获取时，CPU的缓存中的内容被置为无效，从主存中重新读取共享变量的值。
     *
     */
    public static class SynchronizedIdGenerator{
        private int value = 0;
        public synchronized int getNext(){
            return value++;
        }
        public int getNextV2(){
            synchronized (this){
                return value++;
            }
        }
    }

    /**
     * java中的每个对象除了有阈值关联的监视器对象之外，还有一个与之关联的包含线程的等待集合。
     * 在调用wait方法时，该方法调用的接收者所关联的监视器对象是所使用的监视器对象，同时wait方法所影响的是执行wait方法调用的当前线程。
     * 调用wait方法的先决条件是当前线程获取到监视器对象上的锁，否则抛出java.lang.IllegalMonitorStateException。
     * wait方法调用成功后，当前线程被添加到对象所关联的等待集合中，并释放其持有的监视器对象上的锁。当前线程被阻塞，无法继续执行，直到被从对象所关联的等待集合中移除。
     */
    public static void AboutNotifyAndWait() throws InterruptedException {
        Object lock = new Object();

        synchronized (lock){//持有锁
            while(true){ //条件不满足时
                lock.wait();//当前线程进入等待，释放锁
            }
            //条件满足时
        }
    }


    /**
     * 目前CPU基本都提供了相关的指令来实现读取、修改和写入这三步的原子操作。
     * 比较常见的指令名称是"比较和替换（compare and swap，CAS）。
     * 这个命令会先比较某个内存地址的当前值是不是指定的旧值，是才替换，否则就什么也不做。
     * 通过CAS指令可以实现不依赖锁机制的非阻塞算法。
     *
     * java平台利用了CPU提供的CAS指令来实现非阻塞操作（java.util.concurrent.atomic）。在不支持CAS的平台上，atomic包仍通过内部的锁机制来实现。
     * atomic包中提供的java类分成三类：
     *  第一类是支持以原子操作类来进行更新的数据类型，包括：AtomicBoolean、AtomicInteger、AtomicLong、AtomicReference等。
     *  第二类是提供对数组类型的变量进行处理的java类。把数组对象的引用变量声明为volatile只能保证对该引用变量本身的修改对其他线程可见，但不涉及数组中包含的元素。
     *      AtomicIntegerArray、AtomicLongArray和AtomicReferenceArray类把volatile的语义扩展到了数组元素的访问中。
     *  第三类是通过反射的方式对任何对象中包含的volatile变量使用compareAndSet方法进行修改。
     *      AtomicIntegerFieldUpdater、AtomicLongFieldUpdater和AtomicReferenceFieldUpdater类提供一种方式吧compareAndSet扩展到任何java类中声明为volatile的域上。
     */
    public static class AtomicIdGenerator{
        private final AtomicInteger counter = new AtomicInteger();
        public int getNext(){
            return counter.getAndIncrement();
        }
    }

    public static class TreeNode{
        private volatile TreeNode parent;
        private static final AtomicReferenceFieldUpdater<TreeNode,TreeNode> parentUpdater = AtomicReferenceFieldUpdater.newUpdater(TreeNode.class,TreeNode.class,"parent");
        public boolean compareAndSetParent(TreeNode expect,TreeNode update){
            return parentUpdater.compareAndSet(this,expect,update);
        }
    }
}
