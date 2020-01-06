package org.xiaofengcanyue.generic;

import java.lang.reflect.*;
import java.util.List;

/**
 * j2se对java字节代码的格式进行了修改，把泛型相关的信息添加到字节代码中。
 * 不过字节代码中的泛型相关的内容只是提供相关的信息，不会对字节代码的运行造成影响。
 * 在程序中可使用反射API来获取这些信息。
 *
 * 泛型相关的信息是作为类、方法和域的可选属性来保存的。对应的属性名称是"Signature"。
 * 这个属性的值是一个表示类或接口、方法或域的泛型类型签名的字符串。
 *   类或接口的签名由形式类型参数、父类型的签名和所实现的接口的签名组成；
 *   方法的签名由形式类型参数、参数类型签名、返回值类型签名和声明的受检异常类型签名组成；
 *   域的签名是其引用的一般类型、数组类型或形式类型参数的签名。
 *
 */
public class AboutGenericAndReflection {

    /**
     * 使用javap -s -c $path对本内部类的class文件进行反编译。
     */
    public static class GenericSignature <S extends Number>{
        public S obj;
        public void set(S obj){
            this.obj = obj;
        }
        public S get(S obj){
            return obj;
        }

        public static void main(String[] args) {

        }
    }


    /**
     * 在不了解字节代码格式的情况下，通过反射API可以获取相关的信息。
     */
    class Target<T>{
        public List<? extends Comparable<T>> create(T obj){
            return null;
        }
    }

    public static void reflect() throws Exception{
        Class<?> clazz = Target.class;
        Method method = clazz.getMethod("create",new Class<?>[]{Object.class});

        Type paramType = method.getGenericParameterTypes()[1];
        TypeVariable<?> typeVariable = (TypeVariable<?>) paramType;
        String name = typeVariable.getName();
        System.out.println(name);

        Type returnType = method.getGenericReturnType();
        ParameterizedType pType = (ParameterizedType) returnType;
        Type actualType = pType.getActualTypeArguments()[0];
        Type[] bounds = ((WildcardType)actualType).getUpperBounds();
        ParameterizedType boundType = (ParameterizedType) bounds[0];
        Type t = boundType.getRawType();
        System.out.println(t.toString());
    }

    public static void main(String[] args) throws Exception{
        reflect();
    }
}
