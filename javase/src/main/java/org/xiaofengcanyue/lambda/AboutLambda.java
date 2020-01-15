package org.xiaofengcanyue.lambda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * java标准库中存在众多与Runnable接口类似的接口，它们的特征是只声明了一个方法。
 * 具备这样特征的接口被称为函数式接口(functional interface)。
 * 对于函数式接口，通过匿名内部类实现的代码形式可以进一步简化。lambda表达式提供了一种更加简洁的方式来实现函数式接口。
 */
public class AboutLambda {

    /**
     * lambda表达式所表示的对象类型由lambda表达式所出现的山下文环境来确定，由编译器负责进行类型推断，这类似于泛型方法调用和<>操作符中使用的类型推断方式。
     * 为了进行类型推断，lambda表达式所出现的上下文环境中需要能够推断出一个具体类型。这个类型是lambda表达式的目标类型。
     * 可以推断出具体类型的上下文环境包括变量声明、赋值操作、return语句、数组初始化、一般方法或构造方法的参数、lambda表达式方法体、"?:"表达式和强制类型转换表达式等。
     * 如果从当前上下文环境中无法推断出具体的类型，那么lambda表达式无法出现在该上下文环境中。
     * 在确定了目标类型之后，编译器需要检查lambda表达式是否与目标类型兼容。
     * 兼容的条件是该目标类型是一个函数式接口，同时接口中的唯一方法的参数类型、返回值类型和抛出的受检异常都与lambda表达式对应部分保持兼容。
     */
    public Comparator<String> useOfLambda(){
        new Thread(() -> {System.out.println("Hello World");}).start(); //方法参数

        Runnable r = () -> {System.out.println("Run");}; //赋值操作

        return (String s1,String s2) -> s1.compareTo(s2); //return语句
    }

    /**
     * 在使用lambda表达式时需要主要方法体中声明的变量与包含该lambda表达式的上下文环境之间的关系。
     * lambda表达式的方法体并没有引入新的作用域。方法体中的名称与表达式外部的代码处于同一词法作用域中。
     * 在进行解析时，相当于方法体中的名称出现在表达式外部的代码中。而lambda表达式的形式参数也属于表达式外部代码的名称。
     *
     * 本例中lambda表达式中不可重复声明str，但匿名内部类可以。
     */
    public void useOfLambda1(){
        String str = "Hello";
        new Thread(() -> {
//            String str = "World";
            System.out.println(str);
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String str = "World";
                System.out.println(str);
            }
        }).start();
    }

    /**
     * java类中的已有方法也可以被当成是lambda表达式来使用，只需要直接引用以后的方法即可。
     * 方法引用相当于复用了java类中已有方法的方法体。方法引用的使用条件与lambda表达式是相同的。
     * 除了可以引用静态类中的方法外，还可以引用特定对象中的实例方法、特定类型的任意对象中的实例方法及构造方法。
     * 在引用对象的实例方法时，使用对象的引用作为方法引用时的前缀，例如"myObj::myMethod"的形式。
     * 如果引用的是任意对象中的实例方法，那么在引用时，使用方法所在的类型名称作为前缀，如类似"MyClass::myMethod"的形式。此时实际的调用接收者对象会作为方法的第一个参数。
     * 引用构造方法的形式类似于引用类中的静态方法，只不过方法的名称固定位"new"，例如"MyClass:new"。若存在多个构造方法，那么方法引用被使用时的目标类型用于选择使用的构造方法。
     */
    public static class Comparators{
        public static int compareString(String s1,String s2){
            return s1.compareTo(s2);
        }
    }
    public void useOfFuncReference(){
        String[] array = new String[]{"c","b","a"};
        Arrays.sort(array,Comparators::compareString);

        Arrays.sort(array,String::compareToIgnoreCase);//compareToIgnoreCase方法的调用对象会作为第一个参数

//        Arrays.sort(array,new Comparator<String>(){
//
//            @Override
//            public int compare(String o1, String o2) {
//                return 0;
//            }
//        });

        System.out.println(Arrays.deepToString(array));
    }
}
