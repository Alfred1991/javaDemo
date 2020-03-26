package org.xiaofengcanyue.reflection;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.reflect.*;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class AboutReflection {

    public static void main(String[] args) throws Exception{
        classPath();
    }

    /**
     * 由于类型擦除，会造成下面的问题
     */
    public static void problem(){
        ArrayList<String> stringList = Lists.newArrayList();
        ArrayList<Integer> intList = Lists.newArrayList();
        System.out.println(stringList.getClass().isAssignableFrom(intList.getClass()));
        // returns true, even though ArrayList<String> is not assignable from ArrayList<Integer>
    }

    /**
     * TypeToken使用基于反射的方式来允许你在运行时(runtime)操作和查询泛型类型。
     */
    public static void useOfTypeToken() throws Exception{
        TypeToken<String> stringTok = TypeToken.of(String.class);
        TypeToken<Integer> intTok = TypeToken.of(Integer.class);

        TypeToken<List<String>> stringListTok = new TypeToken<List<String>>() {};

        TypeToken<Map<?, ?>> wildMapTok = new TypeToken<Map<?, ?>>() {};

        //动态解析泛型参数
        TypeToken<Map<String, BigInteger>> mapToken = mapToken(
                TypeToken.of(String.class),
                TypeToken.of(BigInteger.class));
        TypeToken<Map<Integer, Queue<String>>> complexToken = mapToken(
                TypeToken.of(Integer.class),
                new TypeToken<Queue<String>>() {});


        TypeToken<Function<Integer, String>> funToken = new TypeToken<Function<Integer, String>>() {};
        TypeToken<?> funResultToken = funToken.resolveType(Function.class.getTypeParameters()[1]);
        // returns a TypeToken<String>

        TypeToken<Map<String, Integer>> mapToken1 = new TypeToken<Map<String, Integer>>() {};
        TypeToken<?> entrySetToken = mapToken1.resolveType(Map.class.getMethod("entrySet").getGenericReturnType());
        // returns a TypeToken<Set<Map.Entry<String, Integer>>>
    }

    static <K, V> TypeToken<Map<K, V>> mapToken(TypeToken<K> keyToken, TypeToken<V> valueToken) {
        return new TypeToken<Map<K, V>>() {}
                .where(new TypeParameter<K>() {}, keyToken)
                .where(new TypeParameter<V>() {}, valueToken);
    }


    /**
     * Invokable is a fluent wrapper of java.lang.reflect.Method and java.lang.reflect.Constructor.
     */
    public static void useOfInvokable() throws Exception{
        Invokable<List<String>, ?> invokable = new TypeToken<List<String>>() {}.method(List.class.getMethod("get",int.class));
        System.out.println(invokable.getReturnType().getType().getTypeName()); // String.class
    }

    public static void dynamicProxies(){
        //jdk
        Object foo = (Object) Proxy.newProxyInstance(
                Object.class.getClassLoader(),
                new Class<?>[]{Object.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return null;
                    }
                });

        //guava
        Object foo1 = Reflection.newProxy(Object.class, new AbstractInvocationHandler() {
            @Override
            protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        });
    }

    /**
     * ClassPath is a utility that offers best-effort class path scanning.
     */
    public static void classPath() throws IOException {
        ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader());
        for(ClassPath.ClassInfo classInfo : classPath.getTopLevelClasses("org.xiaofengcanyue.reflection")){
            System.out.println(classInfo.getName());
            System.out.println(classInfo.url().toString());
            System.out.println(classInfo.getResourceName());
        }
    }

    /**
     * The utility method Reflection.initialize(Class...) ensures that the specified classes are initialized
     * -- for example, any static initialization is performed.
     */
    public static void classLoading() throws Exception{
        Reflection.initialize(Class.forName("AboutReflection"));
    }
}
