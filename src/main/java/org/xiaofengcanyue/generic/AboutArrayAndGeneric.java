package org.xiaofengcanyue.generic;

import java.util.ArrayList;
import java.util.List;

public class AboutArrayAndGeneric {

    /**
     * java中数组对象是由java虚拟机根据元素类型创建出来的。
     * 数组对象不同于集合类对象的一个重要特征是数组是协变的（convariant)。
     * 如果一个数组类型的元素类型是另外一个数组类型的元素类型的子类型，那么这个数组类型同时也是对应的数组类型的子类型。（String[]类型是Object[]的子类型。）
     * 如果尝试向数组中添加类型不兼容的对象，那么在运行时会产生java.lang.ArrayStoreException异常。
     */
    public void storeInArray(){
        Object[] array = new Integer[10]; //Integer[]是Object[]的子类型
        array[0] = "Hello";//抛出异常
    }

    /**
     * 由于数组的这种特殊性，只有可具体化类型才可以用来创建数组。
     */
    public void storeInGenericArray(){
        Object[] array = new ArrayList<?>[10];//new ArrayList<String>[10];无法通过编译
        array[0] = new ArrayList<Integer>();
    }

    /**
     * 需要注意的是，虽然不允许创建数组，但是元素为参数化类型的数组引用是合法的。例如“List<String>[] list=null;”是一个合法的语句。
     * 因为一个非泛型类型可以继承自某个参数化类型，而用这个非泛型类型创建数组是合法的。
     */
    public static class StringArrayList extends ArrayList<String>{
        public void createArray(){
            ArrayList<String>[] array = new StringArrayList[10];
        }
    }

    /**
     * 在调用参数长度可变的方法时，实际参数是通过数组来传递的。
     * 编译器根据实际参数的个数创建一个数组对象，并把实际参数保存在数组中，再把数组传递给方法。
     * 当长度可变参数的类型是不可具体化类型时，这种数组创建方式可能会出现类型安全问题。编译器并没有禁止该行为，只是给出了相关的警告信息。
     */
    public static class Varargs{
        public void varargsMethod(List<String>... values){
            Object[] array = values;
            List<Integer> list = (List<Integer>) array[0];
            list.add(1);
        }

        public void useVarargsMethod(){
            List<String> list = new ArrayList<>();
            list.add("Hello");
            varargsMethod(list);
            String str = list.get(1);
        }

        public static void main(String[] args){
            new Varargs().useVarargsMethod();
        }
    }

}
