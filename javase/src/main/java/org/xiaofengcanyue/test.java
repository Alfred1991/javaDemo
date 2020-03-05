package org.xiaofengcanyue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class test {

    public static void main(String[] args) throws Exception{
        Object obj = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{tt.class}, new Inner());
        if(obj instanceof tt){
            tt t = (tt) obj;
            System.out.println("计算结果:"+t.sum(10,20));
            System.out.println("********************************");
            System.out.println("计算结果:"+t.product(50,22));
        }
    }

    private static class Inner implements InvocationHandler {

        private static tt instance = new ttImp();

        public void preinvoke(Object[] args){
            System.out.printf("参数值:%d,%d\r\n",args);
        }

        public void postinvoke(Object obj){
            System.out.println("返回值:"+obj);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            preinvoke(args);
            Object ret = method.invoke(instance,args);
            postinvoke(ret);
            return ret;
        }

    }

    private interface tt {
        public int sum (int x,int y);
        public int product(int x,int y);
    }

    private static class ttImp implements tt{
        @Override
        public int sum(int x, int y) {
            System.out.println("计算中...");
            return x+y;
        }

        @Override
        public int product(int x, int y) {
            System.out.println("计算中...");
            return x*y;
        }
    }

}
