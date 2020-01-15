package org.xiaofengcanyue.generic;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 在调用泛型方法时，对于其中包含的形式类型参数，通常不需要显示地指定其实际类型，这是因为编译器会根据方法调用时的上下文信息自动推断对应的实际类型。
 * 如果在泛型方法中使用了泛型类型声明中的形式类型参数，方法中的实际类型可以根据参数化类型的实际类型来推断。
 * 如果泛型方法包含在非泛型类型中，或者方法声明中的形式类型参数与包含该方法的泛型类型的形式类型参数无关，那么可以通过两种方式来判断所使用的的实际类型：
 *   1、在方法调用时显示地指定类型；
 *   2、由编译器根据方法调用上下文信息进行推断。
 */
public class AboutTypeInference {

    /**
     * 本例中一个非泛型类中包含一个泛型方法method的声明
     */
    public static class TypeInference{
        public <T> T method(T obj){
            return obj;
        }
        public <T> List<T> createList(){
            return new ArrayList<T>();
        }
    }

    /**
     * 显示地指定类型
     */
    public static void method1(){
        TypeInference typeInference = new TypeInference();
        typeInference.<Serializable>method("Hello");
    }

    /**
     * 类型的自动推断有两种方式：
     *   1、根据方法调用时的实际参数的静态类型来进行推断；
     *   2、当方法调用的结果被赋值给另一个变量时，根据该变量的静态类型进行推断。
     * 其中第一种方式的优先级较高。
     *
     * 本例根据第一种方式进行推断。
     */
    public static void method2(){
        TypeInference typeInference = new TypeInference();
        typeInference.method("Hello");
    }

    /**
     * 在有些方法中，形式类型参数不出现在参数列表中，而是出现在返回值类型中。
     * 若方法调用的结果被赋值给另一个变量，则根据此变量的静态类型来推断实际类型。
     *
     * 本例中根据第二种方式进行推断。
     */
    public static void method3(){
        TypeInference typeInference = new TypeInference();
        List<Integer> list = typeInference.createList();
    }

    /**
     * java se 7把类型推断从方法调用扩展到了对象创建中，即增加了"<>操作符"（diamond operator）。
     * 在调用构造方法时不再需要显示声明类型，具体的类型通过对象引用的类型来进行推断。
     */
    public static void method4(){
        List<String> list = new ArrayList<>();
        /**
         * 创建对象可看成是一种通过构造方法完成的特殊的方法调用形式。
         * 在进行类型判断时，优先考虑调用构造静态方法时的实际参数的静态类型，再考虑对象引用的静态类型。
         * 因此这里的list1实际是一个ArrayList<String>。
         */
        List<? extends Serializable> list1 = new ArrayList<String>();

        Map<List<? extends Number>,Map<String,Long>> map = new HashMap<>();
    }

}
