package org.xiaofengcanyue.ejb;

import java.beans.*;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * JavaBeans的强大之处在于以规范的组件模型作为基础，可以通过工具很方便地进行单个组件的自定义和多个组件的组装。
 * 符合JavaBeans规范的每个组件都包含3类信息：属性、方法和事件：
 *  属性指的是一个组件暴露出来的外观或行为上的特征；
 *  JavaBeans的方法与一般的java方法并没有区别；
 *  事件是组件之间进行交互的方式，某个组件可以发布事件，其他组件可以在该事件上注册监听器。
 */
public class AboutEJB {

    public String main(String s){
        return s+"!";
    }

    public static void main(String[] args) throws Exception{
        //executeExpression();

        xmlEncode();
    }

    /**
     * 一个javabean组件可以通过java.beans.Introspector类来获取组件中的属性、方法和事件等信息。
     *  获取组件信息的方式有两种：
     *      1、开发人员自己提供BeanInfo接口的实现。
     *          系统会根据名称模式来查找组件对应的BeanInfo接口的实现类，比如类目为"com.java7book.My"的组件，查找类名为"com.java7book.MyBeanInfo"的BeanInfo接口的实现类。
     *      2、系统通过反射API来自动发现组件中的信息。
     *          若使用方式1没有查到，则使用方式2。
     *          在找到BeanInfo接口的实现类之后，不会再继续查找该组件的父类来获取信息；而通过反射API的方式则会沿着继承层次结构树一直向上查找父类中的相关信息。
     *
     */
    public static void introspect() throws IntrospectionException{
        BeanInfo beanInfo = Introspector.getBeanInfo(AboutEJB.class, Object.class,Introspector.IGNORE_ALL_BEANINFO);
    }


    /**
     * JavaBeans组件提供了动态执行语句和表达式的能力，主要是为了方便工具的使用者以类似脚本语言的方式对组件进行操作。
     */
    public static void executeExpression() throws Exception{
        Expression expr = new Expression(new AboutEJB(),"main",new Object[]{"alex"});
        expr.execute();
        Object result = expr.getValue();
        System.out.println(result);
    }

    /**
     * javabean的持久化：
     *  以流的形式进行持久化时使用java.io.ObjectInputStream/ObjectOutputStream
     *  以XML文件进行持久化时使用java.beans.XMLEncoder/XMLDecoder
     *
     *
     * @throws IOException
     */
    public static void xmlEncode() throws IOException{
        OutputStream output = Files.newOutputStream(Paths.get("result.xml"), StandardOpenOption.CREATE_NEW);
        try(XMLEncoder encoder = new XMLEncoder(output, StandardCharsets.UTF_8.name(),true,0)){
            encoder.writeObject(new AboutEJB());
        }
    }
}
