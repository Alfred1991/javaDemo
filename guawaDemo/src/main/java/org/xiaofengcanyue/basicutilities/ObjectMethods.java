package org.xiaofengcanyue.basicutilities;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

public class ObjectMethods {

    public static void main(String[] args) {

    }

    /**
     * 用于处理可能为null的对象的比较
     */
    public static void AboutEquals(){
        Objects.equal("a",null);
    }

    /**
     * 提供了一个基于fields产生hashcode的方法
     */
    public static void AboutHashCode(){
        Objects.hashCode("FieldsA","FieldsB","FieldsC");
        java.util.Objects.hash("");
    }

    /**
     * 帮助提升toString方法的撰写效率。
     */
    public static void AboutToString(){
        MoreObjects.toStringHelper("MyObject").add("x",1).toString();
    }

    /**
     * Comparator和Comparable同时存在，这使得会存在一些冗余代码。
     * ComparisonChain用于解决此问题。
     * 它是一个lazy comparison chain，当发现有非0结果后就立即返回，忽略后面的比较。
     */
    public static void AboutCompare(){
        ComparisonChain.start().
                compare("ObjectAFieldA","ObjectBFieldA").
                compare("ObjectAFieldB","ObjectBFieldB").
                result();
    }
}
