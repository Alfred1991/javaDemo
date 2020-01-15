package org.xiaofengcanyue.util;

import java.util.Comparator;
import java.util.Objects;

public class AboutObjects {

    public static void main(String[] args) {
//        compare();

//        equals();

//        hash();

        useToString();

    }

    /**
     * 新增用来操作对象的工具类java.util.Objects，它只包含对对象进行快速操作的静态方法。
     */
    private static class ReverseComparator implements Comparator<Long> {
        @Override
        public int compare(Long o1, Long o2) {
            return o2.compareTo(o1);
        }
    }

    public static void compare(){
        int value1 = Objects.compare(1L,2L,new ReverseComparator());
        System.out.printf("%d",value1);
    }

    /**
     * 使用这种equals的好处是不需要进行null判断。
     *
     * deepEquals的参数都是数组时，会调用java.util.Arrays类的deepEquals进行比较，因此会考虑数组中所有元素的相等性。
     * 在其他情况下deepEquals方法和equals方法的作用是相同的。
     *
     */
    public static void equals(){
        boolean value1 = Objects.equals(new Object(),new Object());
        Object[] array1 = new Object[]{"Hello",1,1.0};
        Object[] array2 = new Object[]{"Hello",1,1.0};
        boolean value2 = Objects.deepEquals(array1,array2);

        System.out.printf("%s %s %s",value1,value2,"Hello" == "Hello");
    }

    /**
     * 获取hash值，如果参数为null则返回0。
     * 注意hashcode1 和 hashcode3 并不相同。
     */
    public static void hash(){
        int hashCode1 = Objects.hashCode("Hello");
        int hashCode2 = Objects.hash("Hello","World");
        int hashCode3 = Objects.hash("Hello");

        System.out.printf("%d %d %d\r\n",hashCode1,hashCode2,hashCode3);

        System.out.printf("%d %d %d","Hello".hashCode(),"HelloWorld".hashCode(),"Hello World".hashCode());

    }

    /**
     * Objects的toString方法
     */
    public static void useToString(){
        //String s=null;
        String str1 = Objects.toString("Hello");
        String str2 = Objects.toString(null," 空对象 ");
        String str3 = Objects.toString(null);
        //String str4 = s.toString();
        System.out.printf("%s\r\n%s\r\n%s",str1,str2,str3);
        //System.out.printf("%s\r\n%s\r\n%s\r\n%s",str1,str2,str3,str4);
    }




}
