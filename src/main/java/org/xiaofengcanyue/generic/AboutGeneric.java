package org.xiaofengcanyue.generic;

import org.omg.CORBA.OBJ_ADAPTER;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * J2SE 5.0引入了泛型，泛型中包含的具体内容比较多，主要包括泛型类型和泛型方法的声明和实例化。
 * 泛型类型与一般类型的区别在于，泛型类型有形式类型参数（type parameter），可以在泛型类型被实例化时替换成实际的具体类型（type argument）。
 * 实例化后的泛型类型被称为参数化类型（parameterized type）。参数化类型分为两类：一类是不带通配符的类型；另外一类是带通配符（？）的类型。
 * 通配符的作用是表示一组类型的集合，在使用它时可以指定其上界和下界。不包含上界或下界的通配符被称为无界通配符（unbounded wildcard）。
 * 在java中，除了枚举类型、匿名内部类型和异常类型之外，其他类型都可以添加形式类型参数，称为泛型类型。
 *
 * 形式类型参数不能用来创建对象和数组、不能作为父类型、不能使用在instanceof表达式中、不能使用其类型字面量、不能出现在异常处理中，以及不能出现在静态上下文中。
 * 这些限制源于java中泛型类型的实现机制，即类型擦除（type erasure）。
 *
 * 为兼容J2SE 5.0之前的遗留代码，泛型类型在使用时可以不指定实际类型，此时所得到的类型被称为原始类型（raw type）。使用原始类型是不安全的操作。
 *
 * 在构造方法或一般方法的声明中也可使用形式参数类型，并称之为泛型方法。
 * 泛型方法与泛型类型并没有直接关系，在调用泛型方法时通常不需要显式指定所用的实际类型。
 * 编译器可以根据方法调用时的实际参数类型和上下文信息进行类型推断。
 */
public class AboutGeneric {

    public static void main(String[] args) {

    }

    /**
     * 一个简单的泛型方法
     */
    public static <S> S generic(S s){
        return s;
    }

    /**
     * 一个简单的泛型类
     */
    public static class ObjectHolder<T>{
        private T obj;
        public T getObj(){
            return obj;
        }
        public void setObj(T obj) {
            this.obj = obj;
        }

        public static void main(String[] args) {
            ObjectHolder<String> holder = new ObjectHolder<>();
            holder.setObj("Hello");
            String str = holder.getObj();
        }
    }

    /**
     * 泛型是在编译器这个层次实现的，在java源代码中声明的泛型类型信息，在编译过程中会被擦除，只保留不带类型参数的形式。
     * 被擦除的类型信息包括泛型类型和泛型方法声明时的形式类型参数，以及参数化类型中的实际类型参数。（毕竟泛型的初衷是为了避免ClassCastException）
     *
     * 在运行时可用的类型被称为可具体化类型（reifiable type），
     * java中的可具体化类型包括非泛型类型、所有实际类型都是无界通配符的参数化类型、原始类型、基本类型、元素类型为可具体化类型的数组类型，以及父类型和自身都是可具体化类型的嵌套类型。
     * 虚拟机在执行字节代码时只能使用运行时可用的可具体化类型。这使Java中与虚拟机相关的语法特性对于不可具体化的参数化类型是不可用的。
     * 例如异常的捕获和处理是由虚拟机来完成的，因此异常类型必须是可具体化的。任何泛型类型都不能直接或间接继承自Throwable类。
     *
     * 在类型擦除过程中需要处理形式类型参数和参数化类型中的实际类型。
     * 对于形式类型参数，在泛型类型声明中的部分会直接剔除，如ObjectHolder<T>被替换为ObjectHolder；在泛型类型代码中出现的则根据上界替换成具体的类型。
     * 如果形式类型参数声明了上界，则声明中最左边的上界作为进行替换的类型；如果没有声明上界，则使用Object类型进行替换。
     * 而对于参数化类型的实际类型，它们在代码中的出现会被直接删除。
     * 进行这些替换之后，可能会出现代码逻辑不合法的情况，编译器会通过插入适当的强制类型转换代码和生成桥接方法（bridge method）来解决
     *
     * 本例给出ObjectHolder经过类型擦除后的形式。
     */
    public static class ObjectHolderAfterErase{
        private Object obj;
        public Object getObj() {
            return obj;
        }
        public void setObj(Object obj) {
            this.obj = obj;
        }

        public static void main(String[] args) {
            ObjectHolderAfterErase holder = new ObjectHolderAfterErase();
            holder.setObj("Hello");
            String str = (String) holder.getObj();
        }
    }

    /**
     * 当一个类继承某个参数化类或实现参数化接口时，在经过类型擦除之后，可能造成所继承的方法的类型签名发生改变。
     * 典型的示例是java.lang.Comparable接口的实现类。
     * 在经过类型擦除之后，Comparable接口的实际类型"<Sequence>"被删除，Sequence类的声明变成了实现原始的Comparable接口。
     * 从接口实现的角度讲，这要求Sequence类中包含一个类型签名为"int compareTo(Object)"的方法。
     * 由于类型擦除，编译器需要添加相应的方法来确保代码实现的正确性，这些由编译器自动天际的方法被称为桥接方法。
     *
     * 虽然自动添加的桥接方法compareTo接受Object类型的参数，但是代码中不能直接使用这个方法。
     * 由于桥接方法在运行时是可见的，因此可通过反射API来查找并调用桥接方法。
     */
    public static class Sequence implements Comparable<Sequence> {
        private final int sequenceNumber;
        public Sequence(int sequenceNumber){
            this.sequenceNumber = sequenceNumber;
        }

        @Override
        public int compareTo(Sequence o) {
            return Integer.compare(sequenceNumber,o.sequenceNumber);
        }

        /**
         * 桥接方法的内部实现：
         * public int compareTo(Object o){
         *             return this.compareTo((Sequence) o);
         * }
         */
    }

    /**
     * 本例给出使用反射API来调用Sequence类中的桥接方法
     */
    public void invoke(){
        try {
            Method method = Sequence.class.getMethod("CompareTo",new Class<?>[]{Object.class});
            method.isBridge(); //值为true
            Sequence seq1 = new Sequence(1);
            Sequence seq2 = new Sequence(2);
            method.invoke(seq1,seq2);
            method.invoke(seq1,"Hello"); //抛出ClassCastException异常。
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 类型擦除的存在影响了很多泛型的特性。
     * 同一泛型类型的所有实例化形式在运行时的表示形式是相同的，每个泛型类型只对应一份字节代码。
     *
     * 除了实际类型都是无上界通配符外的泛型类型的其他实例化形式，都不能用在instanceof操作符中。
     * 这是因为instanceof操作符是根据对象的运行时类型来进行判断的，只对可具体化类型有意义。对于参数化类型来说，只能比较类型擦除之后的类型。
     *
     * 在泛型类型中定义的静态方法和域是被所有的实例化形式的对象所共享的。
     * 在引用泛型类型中定义的静态变量和方法时，直接使用原始类型，不能使用参数化类型。
     */
    public static class StaticField<T>{
        public static int count = 0;
        public StaticField(){
            count++;
        }

        public static void main(String[] args) {
            new StaticField<String>();
            new StaticField<Integer>();
            System.out.println(StaticField.count);
            //StaticField<String>.count是非法的引用形式。
        }
    }


    /**
     * 在通过extends添加了上界之后，泛型类型在实例化时只能使用由上界表示的类型及其子类型。默认的上界是Object类。
     * 形式类型参数虽然有上界，但是没有下界，这是因为下界在实际中几乎没有作用。
     * 一个形式类型参数可以包含多个上界，不同上界之间用&来分隔。
     * 这些上界之间的顺序虽然不会对实例化时所能使用的具体类型的范围产生影响，但会影响类型擦除之后使用的类型。
     * 在类型擦除过程中，形式类型参数会被最左边的上界所代替。
     */
    public class CloneableSerializable<T extends Cloneable & Serializable>{
        public void serialize(T obj){

        }
    }

    /**
     * 参数化类型中的上界和下界都是与通配符一块来使用的，但不能同时有上界和下界。
     * 通配符的含义是一组类型的集合，不同于单个具体的类型。为了表示这种类型，编译器在内部使用通配符捕获（wildcard capture）类型的方式。
     * 通配符捕获类型是一种特殊类型，可以表示通配符所代表的类型集合中的任意类型，它与所有的具体类型都是不兼容的，它只能与其他的通配符捕获类型相兼容。
     * 例如对于一个类型声明为"List<? extends Number>"的变量，编译器在内部实际使用的通配符捕获类型是"List<capture#1-of ? extends Number>"。
     */
    public static class WildCard{
        public List<? extends Number> createList(){
            return new ArrayList<>();
        }
        public void use(){
            List<? extends Object> list = createList();
        }
    }

    /**
     * 在使用包含通配符的参数化类型的对象引用时，通常需要提供额外的类型信息来方便对该对象的使用。
     *
     * 本例通过由参数给出的接口类型的cast方法把创建出来的对象转换成正确的类型。
     * 具体的类型信息由create方法的调用者来提供，这就保证了create方法的返回值是调用者所期望的类型。
     */
    public static class ObjectFactory {
        public static <T> T create(Class<T> interfaceType) throws Exception{
            String className = searchForClassName();
            Class<?> clazz = Class.forName(className);
            return interfaceType.cast(clazz.newInstance());
        }
        private static String searchForClassName(){
            return "";
        }
    }
}
