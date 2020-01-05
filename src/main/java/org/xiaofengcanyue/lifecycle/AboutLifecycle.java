package org.xiaofengcanyue.lifecycle;

/**
 * java类的链接指的是 把加载的java类的字节代码中包含的信息与虚拟机的内部信息进行合并，使java类的代码可以被执行。
 * 链接的过程由3个步骤组成：验证、准备和解析。在链接过程中，会对java类的直接父类或父接口进行验证或准备，但是对类中形式引用的解析是可选的。
 *  1、验证是用来确保java类的字节代码表示在结构上是完全正确的。验证过程有可能会导致其他java类或接口被加载。如果验证过程中发现字节代码格式不正确，会抛出java.lang.VerifyError错误。
 *  2、准备过程会创建java类中的静态域，并设置它们的默认值。准备过程中的一个重要环节是保证类加载时的类型安全。
 *    在链接过程中，可能有两个不同的类加载器对象同时加载一个java类。在加载过程中，这两个类加载器对象也会分别加载java类中的域和方法的参数和返回值引用的其他java类。
 *    从类型安全角度来说，不应该出现一个方法的参数类型对应的java类，以及返回值类型对应的java类由不同的类加载器对象来定义的情况。
 *    在准备过程中，当虚拟机中的某个类加载器对象开始加载某个类时，虚拟机会把该类加载器对象记录为该java类的初始类加载器。
 *    之后虚拟机会马上进行一次检查，若发现刚才的加载操作导致类型安全约束被破坏，则类加载过程不能进行，虚拟机会抛出java.lang.LinkageError错误。
 *  3、解析过程是处理所加载的java类中包含的形式引用。在一个java类中会包含对其他类或接口的形式引用，包括它的父类、所实现的接口、方法的形式参数和返回值的java类等。
 *    在java类中可能包含其他类中的方法调用，在解析过程中需检查所调用的方法确实存在。
 *    解析过程中的一个问题是如何处理复杂的引用关系图。java类之间的引用关系十分复杂，在解析一个java类的过程中，可能导致其他的java类被加载和解析。
 *    通常可利用两种策略来处理：
 *      一种是提前解析，即在链接时递归地对依赖的所有形式引用都进行解析，该做法的缺点是性能较差。
 *      另一种是延迟解析，即只有真正需要一个形式引用时才进行解析。如果一个java类只是被引用，没有在程序运行中被真正用到，这个类就不会被解析。
 *
 * 当java类第一次被真正使用时，虚拟机会对该java类进行初始化。初始化主要执行java类中的静态代码块和初始化静态域（按照在代码中出现的顺序依次执行）。
 * 在当前java类被初始化之前，它的直接父类也会被初始化，但该java类所实现的接口不会被初始化。对一个接口来说，当其被初始化时，它的父接口不会被初始化。
 * java类被初始化的触发条件：
 *  1、创建一个java类的实例对象。
 *  2、调用一个java类的静态方法。
 *  3、为类或接口中的静态域赋值。
 *  4、访问类或接口中声明的静态域，且该域的值不是常值变量。常值变量是声明为final的java基本类型或String类型的变量，使用编译时常量来初始化。
 *  5、在一个顶层java类中执行assert语句也会使该类被初始化。
 *  6、调用Class类和反射API中进行反射操作的方法。
 */
public class AboutLifecycle {

    public static void main(String[] args) {
        //System.out.println(B.value);

        //newObject();

        test();
    }

    /**
     * 当访问一个java类或接口中的静态域时，只有真正声明这个域的类或接口才会被初始化。
     * 本例中类A会被初始化，类B不会
     */
    static class A{
        public static int value = 100;
        static {
            System.out.println("类A初始化。");
        }
    }
    static class B extends A{
        static {
            System.out.println("类B初始化");
        }
    }


    /**
     * 类的构造方法调用过程分为三步：
     *  第一步调用父类的构造方法（显式或隐式）。
     *  第二部初始化类中实例域的值。
     *  第三部是执行类的构造方法中的其他代码.
     */
    static class Animal{
        int legs = 0;
        Animal(int legs){
            this.legs = legs;
        }
    }

    static class Dog extends Animal{
        String name = "<default>";
        Dog(){
            super(4);
        }
    }

    public static void newObject(){
        Dog dog = new Dog();
        System.out.println(dog.legs);
    }

    /**
     * 在编写构造方法时，要注意不要在构造方法中调用可以被子类覆写的方法。
     * 因为当执行父类构造方法调用到子类覆写的方法时，子类的构造方法尚未被执行。
     */
    static class Parent{
        private int average;
        public Parent(){
            average = 30/getCount();
        }
        protected int getCount(){
            return 4;
        }
    }

    static class Child extends Parent{
        private int count;
        public Child(int count){
            this.count = count;
        }
        public int getCount(){
            return count;
        }
    }

    public static void test(){
        Child child = new Child(5);
        System.out.println(child.count);
    }

    /**
     * 为了避免子类finalize方法未调用父类finalize方法的情况，可以使用一种被称为"终止器守卫者（finalizer guardian）"的模式。
     * WithFinalizer类的自定义的对象终止逻辑被添加到WithFinalizer类的一个实例域guardian的finalize方法中。
     * 当WithFinalizer类的对象可以被回收时，guardian对象也同样可以被回收。此时guardian对象的finalize方法会被调用。
     * 即便WithFinalizer类的子类没有调用super.finalize方法，WithFinalizer类的对象也能被正确终止。
     */
    public static class WithFinalizer{
        private final Object guardian = new Object(){
            @Override
            protected void finalize() throws Throwable {
                //WithFinalizer类的对象终止实现
                super.finalize();
            }
        };
    }

    /**
     * Object类的clone方法复制对象的做法是对当前对象中所有的实例域进行逐一复制。
     * 先创建一个新的对象，再把新对象中所有的实例域的值初始化成原始对象中对应域的当前值。
     * 该方法一般是使用原生代码实现的。可以将其视为一种浅拷贝。
     */
    public static class ToBeCloned implements Cloneable{
        private int value = 0;
        public void setValue(int value){
            this.value = value;
        }
        public int getValue(){
            return this.value;
        }
        @Override
        public Object clone(){
            try{
                return super.clone();
            }catch (CloneNotSupportedException e){
                throw new Error(e);
            }
        }

        public static void main(String[] args) {
            ToBeCloned obj = new ToBeCloned();
            obj.setValue(1);
            ToBeCloned clonedObj = (ToBeCloned) obj.clone();
            System.out.println(clonedObj.getValue());
        }
    }

    /**
     * 如果对象中某些域的值为可变对象，浅拷贝就不能满足需求。
     * 因为所复制出来的对象的域与原始对象的域使用相同的对象引用。指向的是同一个对象。
     *
     * 解决方案是使用深拷贝，即对所有可变对象都执行clone，且所有可变对象都要实现clonable。
     */
    public static class Counter{
        private int value = 0;
        public void increase(){
            value++;
        }

        public int getValue() {
            return value;
        }
    }
    public static class MutableObject implements Cloneable{
        private Counter counter = new Counter();
        public void increase(){
            counter.increase();
        }
        public int getValue(){
            return counter.value;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            try{
                return super.clone();
            }catch (CloneNotSupportedException e){
                throw new Error(e);
            }
        }

        public static void main(String[] args) throws CloneNotSupportedException {
            MutableObject obj = new MutableObject();
            obj.increase();
            MutableObject clonedObj = (MutableObject) obj.clone();
            clonedObj.increase();
            obj.increase();
            System.out.println(clonedObj.getValue());
        }
    }

    /**
     * 进行对象复制的另外一个做法是使用复制构造方法。
     */
    public static class User{
        private String name;
        private String email;
        public User(String name,String email){
            this.name=name;
            this.email=email;
        }
        public User(User user){
            this.name=user.getName();
            this.email=user.getEmail();
        }
        public String getName(){
            return this.name;
        }

        public String getEmail() {
            return email;
        }
    }

}

