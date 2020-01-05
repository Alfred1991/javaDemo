package org.xiaofengcanyue.jvm;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryPoolMXBean;
import java.util.List;

/**
 * 从java SE 5.0开始，java平台提供了一套完整的API来对运行的Java程序及虚拟机本身进行监控和管理，这一套API中最重要组成部分即是Java Management Extensions,JMX。
 * JMX API在很多情况下都可以发挥作用，包括在运行时动态获取和更新程序的配置信息、收集程序运行过程中的统计数据以及当程序内部状态发生变化或出现错误时发出相关通知等。
 * JMX API的基础概念是MBean，一个MBean表示的是可以被管理的命名资源。每个MBean都提供一个管理接口允许第三方来使用。这个管理接口的内容包括可以获取和修改值得命名属性、可以调用的命名方法，以及可以发出的事件通知。
 * 所有的MBean实现被注册到MBean服务器上，使用者通过名称在MBean服务器上查找所需的MBean实现，之后便可通过MBean的管理接口来调用其中的方法。
 * 可以被MBean监控和管理的资源包括虚拟机中的缓冲区、类加载系统、代码编译、垃圾回收器、内存、底层操作系统、虚拟机运行时、线程和日志记录器等。
 * 通过java.lang.management.ManagementFactory类中的工厂方法可以得到各类MBean实现，比如对线程的监控和管理是由接口java.lang.management.ThreadMXBean类表示的。
 *
 *
 *
 */
public class AboutJMX {

    /**
     * 根据虚拟机内存空间情况判断是否需要执行备份任务。
     */
    public static class BackupTaskRunnable implements Runnable{
        private MemoryPoolMXBean poolBean;

        public BackupTaskRunnable(){}

        private void init(){
            List<MemoryPoolMXBean> beans = ManagementFactory.getMemoryPoolMXBeans();
            for(MemoryPoolMXBean bean : beans){
                if("Tenured Gen".equals(bean.getName())){
                    poolBean = bean;
                    break;
                }
            }
            poolBean.setUsageThreshold(10 * 1024 * 1024);
        }

        @Override
        public void run() {
            while(true){
                if(poolBean.isUsageThresholdExceeded()){
                    System.out.println("内存不足，暂停备份任务。");
                }else{
                    System.out.println("执行备份任务。");
                }
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 上面代码使用轮询方式判断内存使用量是否超过阈值，本例采用监听机制。
     * 某些MBean在内部状态发生变化或出现错误的时候，会产生相应的事件通知。
     * MBean的使用者可以在事件通知上注册监听器。
     */
    private static class MemoryListener implements NotificationListener{
        @Override
        public void handleNotification(Notification notification, Object handback) {
            String type = notification.getType();
            if(type.equals(MemoryNotificationInfo.MEMORY_COLLECTION_THRESHOLD_EXCEEDED)){
                System.out.println("内存占用量超过阈值。");
            }
        }
    }

    public static void addListener(){
        MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
        NotificationEmitter emitter = (NotificationEmitter) mbean;
        MemoryListener listener = new MemoryListener();
        emitter.addNotificationListener(listener,null,null);
    }
}
