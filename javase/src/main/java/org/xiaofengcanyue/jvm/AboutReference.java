package org.xiaofengcanyue.jvm;

import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Java程序中最常见的引用类型是强引用（strong reference），它也是默认的引用类型。
 *  使用new创建一个新的对象，并将其赋值给一个变量的时候，这个变量就成为指向该对象的一个强引用。
 *
 * 判断一个对象是否存活的标准为 是否存在指向这个对象的引用。垃圾回收期可能采取不同算法来做相关的判断。
 *  一个常见的做法是使用引用计数器。但这个做法无法解决引用孤岛的问题。
 *  java虚拟机的垃圾回收期采取跟踪对象引用的做法。该做法从虚拟机内存中的某些存活对象开始，递归检查它们所引用的其他对象，直到找到不引用其他对象的对象为止。
 *  这个遍历过程的起始对象是一个集合（根集合），一般包含系统类、程序寄存器、JNI全局引用、静态变量和线程的当前活动栈中的变量所指向的对象等。
 *
 * 由于垃圾回收器的存在，java中没有真正意义上的内存泄漏。
 * 但某些错误的用法会对程序中所能使用的内存空间造成影响，这可以看做是另一种意义上的内存泄漏，这些内存泄漏的发生都与强引用有关。
 *
 */
public class AboutReference {

    public static void main(String[] args) {
//        useReference();

//        useSoftReference();

        UseReferenceQueue.phantomReferenceQueue();
    }

    /**
     * java中内存泄漏一:
     * 一种是虚拟机中存在程序无法使用的内存区域。
     * 这些区域被程序中一些无法使用的存活对象占用。
     * 这些对象由于存在隐式的强引用，无法对其进行垃圾回收。造成该问题的原因通常是程序编写时的逻辑错误。
     *
     * 本例中backendList中小于topIndex的内存区域都属于闲置状态。
     * @param <T>
     */
    public static class LeakingQueue<T>{
        private List<T> backendList = new ArrayList<>();
        private int topIndex = 0;
        public void enqueue(T value){
            backendList.add(value);
        }
        public T dequeue(){
            T result = backendList.get(topIndex);
            topIndex++;
            return result;
        }
    }

    /**
     * java中内存泄漏二：
     *  另一种情况是程序中存在大量存活时间过长的对象。通常这些对象在引用它们的对象被回收之后，也应该被回收，但由于某些程序中的错误而没有被回收。
     *
     * 本例中cache的存在使得其中的对象无法被回收。
     */
    public static class Calculator{
        private Map<String,Object> cache = new HashMap<>();
        public Object calculate(String expr){
            if(cache.containsKey(expr)){
                return cache.get(expr);
            }
            Object result = doCalculate(expr);
            cache.put(expr,result);
            return result;
        }
        private Object doCalculate(String expr){
            return new Object();
        }
    }


    /**
     * 所有的引用类型都是java.lang.ref.Reference类的子类。
     * 引用一个对象的通常做法如下，这相当于在该对象上添加了一个强引用。
     */
    public static void useReference(){
        Object obj = new Object();
        System.out.println(obj instanceof Reference);
    }


    /**
     * 创建完软引用，需要显式地清除原来的强引用。
     * 此时对Object对象进行垃圾回收变为可能。
     *
     */
    public static void useSoftReference(){
        Object obj = new Object();
        SoftReference<Object> ref = new SoftReference<>(obj);
        obj = null;
        System.out.println(ref instanceof Reference);

        //若对象被回收则返回null
        if(ref.get() != null){
            Object object = ref.get();
        }

        //清除这个引用
        ref.clear();
    }

    /**
     * 没有使用强引用先指向待引用的对象，而垃圾回收期又随时可能进行回收。
     * 可能出现的一种情况是：在SoftReference类的对象创建出来之后，垃圾回收器正好回收了SoftReference类的对象所指向的对象，这会使该引用对象实际上毫无意义。
     */
    public static void wrongWayToUseSoftReference(){
        SoftReference<Object> ref = new SoftReference<>(new Object());
    }

    /**
     * 引用队列是一个包含了引用类型对象的先进先出的队列。
     * 在创建时关联了引用类型对象 和 引用队列，垃圾回收器会在合适的时间点上把引用对象放入到对应的队列中。
     */
    public static void useReferenceQueue(){
        Object obj = new Object();
        ReferenceQueue queue = new ReferenceQueue();
        SoftReference<Object> ref = new SoftReference<>(obj,queue);
        obj = null;
    }

    /**
     * 如果一个对象不是强引用可达的，同时可以通过软引用来访问，那么将这个对象称为 软引用可达（softly reachable）。
     * 软引用索要传递给垃圾回收器的信息是：软引用所指的对象是可以在需要的时候被回收的。GC会保证在抛出OutOfMemoryError错误之前，回收掉所有软引用可达的对象。
     */
    public static class FileEditor{
        private static class FileData{
            private Path filePath;
            private SoftReference<byte[]> dataRef;

            public FileData(Path filePath){
                this.filePath = filePath;
                this.dataRef = new SoftReference<>(new byte[0]);
            }

            public Path getPath(){
                return filePath;
            }

            public byte[] getData() throws IOException{
                byte[] dataArray = dataRef.get();
                if(dataArray == null || dataArray.length == 0){
                    dataArray = readFile();
                    dataRef = new SoftReference<>(dataArray);
                    dataArray = null;
                }
                return dataRef.get();
            }

            private byte[] readFile() throws IOException{
                return Files.readAllBytes(filePath);
            }
        }
        private FileData currentFileData;
        private Map<Path,FileData> openedFiles = new HashMap<>();

        public void switchTo(String filePath){
            Path path = Paths.get(filePath).toAbsolutePath();
            if(openedFiles.containsKey(path)){
                currentFileData = openedFiles.get(path);
            }else{
                currentFileData = new FileData(path);
                openedFiles.put(path,currentFileData);
            }
        }

        public void useFile() throws IOException{
            if(currentFileData != null){
                System.out.println(String.format("当前文件%1$s的大小为%2$d",currentFileData.getPath(),currentFileData.getData().length));
            }
        }
    }

    /**
     * 弱引用在强度上弱于软引用。
     * 弱引用传递给垃圾回收器的信息是：在判断一个对象是否存活时，可以不考虑弱引用的存在。类似地也定义了弱引用可达（weekly reachable）。
     *
     * 比较典型的例子是在哈希表中使用弱引用：
     *  下面的HashMap类的对象具有对所包含的键和值得对象的强引用，这会使book和user对象的存活时间变得至少和HashMap类的对象本身一样长。
     *  java标准库提供了java.util.WeakHashMap，它使用弱引用来指向这些对象。
     */
    public static class BookKeeper{
        private Map<Object, Set<Object>> books = new HashMap<>();
        public void borrowBook(Object book,Object user){
            Set<Object> users = null;
            if(books.containsKey(book)){
                users = books.get(book);
            }else{
                users = new HashSet<>();
                books.put(book,users);
            }
        }
        public void returnBook(Object book,Object user){
            if(books.containsKey(book)){
                Set<Object> users = books.get(book);
                users.remove(user);
            }
        }

    }

    /**
     * 幽灵引用是强度最弱的一种引用类型。它的主要目的是在一个对象所占的内存被实际回收之前得到通知，从而可以进行一些相关的清理工作。
     * 幽灵引用在创建时必须提供一个引用队列作为参数；幽灵引用对象的get方法总是返回null，因此无法通过幽灵引用来获取被引用的对象。
     * 幽灵引用只能通过引用队列来操作，它最大的优势在于 引用对象被添加到队列中的时机。
     *
     * 由于finalize方法的存在，虚拟机中的对象一般处于三种可能的状态：可达状态、可复活状态 和 不可达状态。
     *
     * 当一个对象变成软引用可达或弱引用可达的时候，指向这个对象的引用对象就可能被添加到引用队列中。
     * 在添加到队列之前，gc会清除掉这个引用对象的引用关系。
     * 当软引用和弱引用进入队列之后，对象的finalize方法可能还没有被调用。
     * 在finalize方法执行之后，该对象有可能重新回到可达状态。若该对象回到可达状态，而指向该对象的软引用或若引用对象的引用关系已经被清除，那么就无法再通过引用对象来查找这个对象。
     *
     * 而幽灵对象则不同，只有在对象的finalize方法被运行之后，幽灵引用才会被添加到队列中。
     * 幽灵引用在被添加到队列之前，gc不会自动清除其引用关系，需要通过clear方法来显式地清除。
     * 当幽灵引用被清除之后，对象就进入了不可达状态，gc可以回收其内存。
     * 由于幽灵引用的get方法总返回null，程序不能读幽灵引用所指向的对象进行任何操作。这就避免了finalize方法可能会出现的对象复活问题。
     * 幽灵引用是作为一个通知机制而存在的，程序应该在得到通知之后进行与当前对象相关的清理工作。
     *
     */
    public static class UseReferenceQueue{
        private static class ReferencedObject{
            protected void finilize() throws Throwable{
                System.out.println("finalize方法被调用。");
                super.finalize();
            }
        }
        public static void phantomReferenceQueue(){
            ReferenceQueue<ReferencedObject> queue = new ReferenceQueue<>();
            ReferencedObject obj = new ReferencedObject();
            PhantomReference<ReferencedObject> phantomRef = new PhantomReference<>(obj,queue);
            obj = null;
            Reference<? extends ReferencedObject> ref = null;
            while((ref = queue.poll()) == null){
                System.gc();
            }
            phantomRef.clear();
            System.out.println(ref == phantomRef);
            System.out.println("幽灵引用被清除。");
        }
    }

    /**
     * 在虚拟机内存总量受限的情况下，需要等待一个内存空间较大的对象被回收之后再申请新的内存空间。
     */
    public static class PhantomAllocator{
        private byte[] data = null;
        private ReferenceQueue<byte[]> queue = new ReferenceQueue<>();
        private Reference<? extends byte[]> ref = null;
        public byte[] get(int size){
            if(ref == null){
                data = new byte[size];
                ref = new PhantomReference<>(data,queue);
            }else{
                data = null;
                System.gc();
                try{
                    ref = queue.remove();//此时会被阻塞，直到queue中出现新的对象。且幽灵引用的对象在进队列之前已经执行完了finalize方法。
                    ref.clear();
                    ref = null;
                    System.gc();
                    data = new byte[size];
                    ref = new PhantomReference<>(data,queue);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            return data;
        }
    }

    /**
     * 引用队列的主要作用是作为一个通知机制
     * 当对象的可达状态发生变化时，如果程序希望得到通知，可以使用引用队列。
     * 当从引用队列中获取了引用对象之后，不可能再获取所指向的具体对象(所有引用类型都是如此)。
     */
    private static void expungeStaleEntries() {

        Map<String,String> map = new WeakHashMap<>();

    }
}
