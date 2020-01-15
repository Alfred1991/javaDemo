package org.xiaofengcanyue.concurrent;


import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 虚拟机启动后，通常只有一个普通线程来运行程序代码，该线程用来启动主java类的main方法运行。
 * 除了普通线程外，还有一类线程是在后台运行的守护线程（daemon thread），当虚拟机中运行的所有线程都是守护线程时，虚拟机终止运行。
 *
 * 常见的同步关系如下：
 *  1、在一个监视器对象上的解锁动作与相同对象上后续成功的加锁动作保持同步。
 *  2、对一个声明为volatile的变量的写入动作与同一变量的后续读取动作保持同步。
 *  3、启动一个线程的动作与该线程执行的第一个动作保持同步。
 *  4、向线程中共享变量写入默认值的动作与该线程执行的第一个动作保持同步。即在线程运行之前，该线程所使用的全部对象从概念上来说已经被创建出来，并且对象中的变量被赋予了默认值。
 *  5、线程A运行时的最后一个动作与另外一个线程中任何可以检测到线程A终止的动作保持同步。
 *  6、若线程A中断线程B，那么线程A的中断动作与任何其他线程检测到线程B处于被中断的状态的动作保持同步。
 *
 * happens-before包括：
 *  1、若两个动作A和B在一个线程中执行，同时在程序顺序中A出现在B之前，则A在B之前发生。
 *  2、一个对象的构造方法的结束在该对象的finalize方法运行之前发生。
 *  3、若动作A和动作B保持同步，则A在B之前发生。
 *  4、如果动作A在动作B之前发生，同时动作B在动作C之前发生，则A在C之前发生。
 */
public class AboutThread {

    /**
     * 在写入volatile变量之后，CPU缓存中的内容会被写回主存；在读取volatile变量时，CPU缓存中的对应内容被置为失效装填，重新从主存中进行读取。
     */
    public static class Worker{
        private volatile boolean done;
        public void setDone(boolean done){
            this.done = done;
        }
        public void work(){
            while(!done){
                //执行任务
            }
        }
    }

    /**
     * java中对非long型和double型的域的读取和写入操作时原子操作。
     * 对象引用的读取和写入操作也是原子操作。
     * 在写入非volatile的long型和double型的域的值时，分两次操作来完成。
     * 因此在多线程程序中使用long型和double型的共享变量时，需要将其声明为volatile。
     */
    public static class AtomOp{
        private volatile long l;
        private volatile double d;

        public double getD() {
            return d;
        }

        public void setD(double d) {
            this.d = d;
        }

        public long getL() {
            return l;
        }

        public void setL(long l) {
            this.l = l;
        }
    }

    /**￿
     * Thread.State中包含的线程状态有以下几种：
     *  1、NEW：线程刚被创建。
     *  2、RUNNABLE：线程可运行。
     *  3、BLOCKED：线程在等待一个监视器对象上的锁。
     *  4、WAITING：没有超时时间的等待。
     *  5、TIMED_WAITING：有超时时间的等待。
     *  6、TERMINATED：线程的运行已经终止。
     *
     *  使用Thread类对象的interrupt方法可以向该线程发出中断请求。
     *  在一般情况下，中断一个线程会在该线程对应的Thread类对象上设置一个标记用来记录当前的中断状态。
     *  中断请求不是必须被处理，一个线程可以选择忽略中断请求。
     *  Object类的wait方法和Thread类的join和sleep方法都会抛出受检异常java.lang.InterruptedException。
     *  当线程由于调用上述三个方法进入等待状态时，通过interrupt方法中断该线程会导致该线程离开等待状态。
     *  对于wait方法调用来说，线程需要在重新获取到监视器对象上的锁之后才能抛出InterruptedException并执行异常处理。
     *  在InterruptedException发生时，当前线程对应的Thread类的对象内部的中断标记会被清空。
     *  如果一个线程当前处于某个对象所关联的等待集合中，那么中断该线程或发出唤醒通知都可以使该线程从等待集合中被移除。
     *
     *  Thread的join方法允许当前线程等待另一个线程运行结束。
     *
     *  如果当前线程因为某些原因无法继续执行，可以使用yield方法来尝试让出所占用的CPU资源。
     */
    public static void useJoin(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
