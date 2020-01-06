package org.xiaofengcanyue.generic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AboutTypeSystem {

    /**
     * 在java语言中，类型系统描述了不同类型之前的转换关系。泛型的引入对java中类型系统产生了比较大的影响。
     * 因为泛型类型的实例化形式中包含了所使用的实际类型，这些类型之间也可以有父子类型关系。
     * 这相当于把类型系统从之前的一维结构扩展为二维结构：一个维度是泛型类型本身，另一个维度是参数化类型中的实际类型。
     *
     * 1、当两个参数化类型的实际类型完全相同时，两个类型的父子类型关系取决于泛型类型本身的父子关系。例如ArrayList<Number>是List<Number>的子类型。
     *   这条规则对于包含通配符的情况也是适用的，所以ArrayList<? extends Number>是List<? extends Number>的子类型。
     * 2、如果泛型类型相同，在实例化时使用的是不包含通配符的两个不同的具体类型，则这两个类型之间不存在任何父子类型的关系。
     *   例如ArrayList<Integer>和ArrayList<Number>两个类型之间不存在父子类型的关系。
     * 3、在包含通配符时，使用无界通配符的参数化类型是所有该泛型类型的其他实例化形式的父类型，因为无界通配符表示的是所有可能的类型的集合。
     *   例如List<?>是所有List泛型类的实例化形式的父类型。
     *   使用有界通配符的情况下则取决于具体使用的上界或下界的类型。
     *   例如List<? extends Integer>是List<? extends Number>的子类型；List<? super Number>是List<? super Integer>的子类型。
     * 4、有些泛型类型包含多个形式类型参数，此时需对其每个参数都使用3中的规则进行判断。
     * 5、在参数化类型的单个类型中也可以多次使用通配符，例如List<? extends List<? extends Number>>。此时需要按照递归的方式依次进行判断。
     *   例如List<? extends List<Integer>>是List<? extends List<? extends Number>>的子类型。
     * 6、一个泛型类型的所有实例化形式是其对应的原始类型的子类型。
     *   例如List<String>和List<? extends Number>都是List的子类型。
     *
     * 父子类型关系是传递的。
     * 比如ArrayList<Integer>是Collection<Integer>的子类型，Collection<Integer>是Collection<? extends Number>的子类型，所以ArrayList<Integer>是Collection<? extends Number>的子类型。
     */
    public static class ModifyList{
        public void modify(ArrayList<Number> list){
            list.add(1.0f);
        }
        public void changeList(){
            ArrayList<Integer> list = new ArrayList<>();
            list.add(3);
            //modify(list);编译错误
            Integer value = list.get(1);
        }
    }

    /**
     * 方法覆写：
     *   1、方法类型签名包括方法名称和参数类型两个部分。子类型中的方法覆写父类型中的方法的条件是两个方法的类型签名是相同的，或者父类型中的方法在类型擦除之后的类型签名与子类型中方法的类型签名相同。
     *   2、子类型中方法的返回值类型必须可以代替父类型中对应方法的返回值类型。
     *   3、子类型的方法声明中不能抛出父类型中对应方法没有声明的受检异常。
     */
    public static class SuperClass{
        public void method(List<?> param){
            System.out.println("parent");
        }
    }
    public static class SubClass extends SuperClass{
        @Override
        public void method(List<?> param) {
            System.out.println("child");
        }
        public static void main(String[] args) {
            SubClass subClass = new SubClass();
            subClass.method(new ArrayList<String>());
        }
    }

    /**
     * 在覆写过程中，若父类型和子类型中有一个或另个都是泛型类型时，参数类型是否相同的判断变得非常复杂。这是因为在方法类型签名中可能使用泛型类型定义中的形式类型参数。
     * 当父类型和子类型均为非泛型类型时：
     *   对于父类型中的非泛型方法，子类型中类型前面相同的非泛型方法可以进行覆写。
     *   但是子类型中的泛型方法无法覆写父类型中的非泛型方法。
     *   对于父类型中的泛型方法，子类型中的非泛型方法可以对其进行覆写，只要子类型中的非泛型方法的类型签名与父类方法在类型擦除之后的类型签名相同即可。
     *   对于父类型中的泛型方法，子类型中的泛型方法也可以对其进行覆写，要求子类型中的方法与父类型中的方法的类型签名相同。
     *
     * 本例中父类型的method方法的形式类型参数没有上界，因此在类型擦除后的类型为Object，与子类型方法中的参数类型相同，于是可以覆写。
     */
    public static class SuperClass1{
        public <T> void method(T obj){
            System.out.println("parent");
        }
    }
    public static class SubClass1 extends SuperClass1{
        @Override
        public void method(Object obj) {
            System.out.println("child");
        }
        public static void main(String[] args) {
            new SubClass1().method("");
        }
    }

    /**
     * 本例中两个方法的类型签名中的形式类型参数都表示的是继承自Object类的所有类型，因此这两个类型签名是相同的,可以覆写。
     */
    public static class SuperClass2{
        public <T> void method(T obj){
            System.out.println("parent");
        }
    }
    public static class SubClass2 extends SuperClass2{
        @Override
        public <S> void method(S obj) {
            System.out.println("child");
        }
        public static void main(String[] args) {
            new SubClass2().method("");
        }
    }

    /**
     * 当父类型为非泛型类型，子类型为泛型类型时：
     *   如果子类型中的方法不使用在类型定义时声明的形式类型参数，则与第一种情况相同。
     *   如果子类型中的方法使用了在类型定义时声明的形式类型参数，则无法覆写父类型中的对应方法。
     */
    public static class SuperClass3{
        public void method(Object obj){
            System.out.println("parent");
        }
    }
    public static class SubClass3<S> extends SuperClass3{
        /**
         * @Override
         *         public void method(S obj) {
         *             System.out.println("child");
         *         }
         * 无法通过编译
         */
    }

    /**
     * 当父类型为泛型类型，子类型为非泛型类型时：
     *   子类型中的非泛型方法可以覆写父类型中的泛型方法。因为子类型在继承父类型时需要声明父类型中形式类型参数的实际值。
     */
    public static class SuperClass4<T>{
        public void method(T obj){
            System.out.println("parent");
        }
    }
    public static class SubClass4 extends SuperClass4<Number>{
        @Override
        public void method(Number obj) {
            System.out.println("child");
        }
        public static void main(String[] args) {
            new SubClass4().method(1);
        }
    }

    /**
     * 当父类型和子类型均为泛型类型时：
     *   需判断实际类型是否兼容，并且是否会带来类型安全问题。
     *
     * 本例中父类型和子类型中的泛型方法method都使用了类型定义中的形式类型参数。在对子类型实例化时，形式类型参数S的值同时也是父类型中形式类型参数T的值。
     * 因此两个类型中的method方法的类型签名实际上是相同的。
     */
    public static class SuperClass5<T>{
        public void method(T obj){
            System.out.println("parent");
        }
    }
    public static class SubClass5<S> extends SuperClass5<S>{
        @Override
        public void method(S obj) {
            System.out.println("child");
        }
        public static void main(String[] args) {
            new SubClass5().method(1);
        }
    }

    /**
     * 本例中覆写关系不会引起类型安全问题，因此仍能覆写
     */
    public static class SuperClass6<T>{
        public void method(T obj){
            System.out.println("parent");
        }
    }
    public static class SubClass6<S extends Number> extends SuperClass6<S>{
        @Override
        public void method(Number obj) {
            System.out.println("child");
        }
        public static void main(String[] args) {
            new SubClass6().method(1);
        }
    }

    /**
     * 本例中父类型method方法的返回值类型是List<? extends Serailizable>，而子类型中方法的返回值类型实际是List<? extends Number>。
     * 由于List<? extends Number>是List<? extends Serializable>的子类型，因此这是一个正确的覆写。
     */
    public static class SuperClass7<T>{
        public List<? extends Serializable> method(){
            System.out.println("parent");
            return null;
        }
    }
    public static class SubClass7<S extends Number> extends SuperClass7<S>{
        @Override
        public List<S> method() {
            System.out.println("child");
            return null;
        }

        public static void main(String[] args) {
            new SubClass7().method();
        }
    }

    /**
     * 当两个方法的名称相同，但方法类型签名不满足覆写条件时，这两个方法是不同的重载形式。
     */
}
