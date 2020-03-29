package org.xiaofengcanyue.jvm;

import jdk.internal.org.objectweb.asm.ClassWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 关于gc：https://blog.csdn.net/renfufei/column/info/14851
 */
public class AboutHotSpot {

    public static void main(String[] args) {
        StringIntern.useInternString();
    }

    /**
     * 虚拟机在执行字节代码时一般采用即使编译方式，即所谓Just-in-time(JIT)编译方式。
     * 虚拟机会在运行过程中将字节代码中的指令直接转换成底层操作系统平台上的原生指令。
     * 该方式有一些性能方面的问题，hotspot虚拟机采用自适应的优化技术来解决该问题，其中的关键是利用程序运行中的热点（hotspot）。
     *
     * 程序运行过程中的一个重要特征是程序局部性，即小部分代码会占用较多的运行时间，这小部分代码被称为热点。
     * 在程序刚开始运行时，hotspot虚拟机会分析程序的字节码，以找出其中的热点，并对其进行复杂的优化工作。随着程序的运行，热点可能会发生变化，虚拟机会追踪其中的热点。
     *
     * hotspot虚拟机的另外一个优化措施是方法内联，其作用是把被调用的方法中的代码直接内联到调用的地方。这样可以减少方法调用，同时为虚拟机提供更多可以优化的代码。
     */
    public void executeClass(){

    }

    /**
     * hotspot虚拟机提供了多种不同的垃圾回收算法，这些算法都采用了分代回收的方式。
     *
     * hotspot虚拟机将内存划分为3个世代：年轻、年老和永久世代。永久世代中包含的是java虚拟机自身运行所需的对象。
     * 对于年老和永久世代内存区域，通常采用mark-sweep-compact算法。
     */
    public void garbageCollect(){

    }

    /**
     * 虚拟机对字符串的内部化处理，可能会造成永久世代的内存不足。
     * 由于java中的String类的对象是不可变的，java提供了一种字符串内部化的机制。
     * 该机制在虚拟机中缓存String类的对象，当需要使用包含相同字符串的String类对象的时候，可以直接使用缓存中的对象。
     * 这样只需要简单地使用"=="操作符就可以比较两个String对象是否相等。
     * 有些虚拟机中，缓存的String对象是保存在永久世代中的。如果使用太多内部化String对象，对象又都处于被引用的状态，就会导致永久世代内存不足。
     *
     * 本例通过循环生成包含随机内容的字符串，并调用String累的intern方法来缓存这些字符。
     */
    public static class StringIntern{
        private static List<String> list = new ArrayList<>();
        public static void useInternString(){
            Random random = new Random();
            for(int i = 0; i < 200; i++){
                char[] data = new char[128 * 1024];
                for(int j = 0;j < data.length;j++){
                    data[j] = (char) random.nextInt(32768);
                }
                list.add(new String(data).intern());
            }
        }
    }

    /**
     * 另一个造成永久世代内存不足的原因是加载的java类过多。
     *
     * 本例通过ASM工具创建一个简单的java类的字节代码，将其保存在一个字节数组中。
     * 在调用了defineClass之后，java类的元数据被保存在永久世代中。
     */
    public static class LoadClass extends ClassLoader {

        public void loadManyClasses(){
            int num = 50000;
            String classNamePrefix = "ManyClass";
            for(int i = 0;i<num;i++){
                String className = classNamePrefix + i;
                createAndLoadClass(className);
            }
        }

        private void createAndLoadClass(String className){
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            cw.visit(1,1,className,null,"java/lang/Object",null);
            cw.visitEnd();
            byte[] classData = cw.toByteArray();
            this.defineClass(className,classData,0,classData.length);
        }
    }


}
