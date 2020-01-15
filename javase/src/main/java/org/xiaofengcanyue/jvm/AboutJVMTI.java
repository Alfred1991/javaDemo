package org.xiaofengcanyue.jvm;

/**
 * JMX API中MBean对虚拟机进行监控和管理的范围有限。J2SE 5.0对虚拟机中与监控和管理相关的功能进行整合，形成了标准的Java虚拟机工具接口(Java Virtual Machine Tools Interface,JVM TI)。
 * JVM TI主要供开发、调试和监控工具使用，它通过C/C++原生代码来使用。
 * 使用JVM TI开发的工具被称为虚拟机上的代理程序（agent）。在虚拟机启动时，代理程序也会被加载和运行，代理程序和JVM在同一个进程中运行。
 * 代理程序本身是底层操作系统平台上的一个原生代码库，在虚拟机启动时通过参数"-agentlib"或"-agentpath"来指定原生代码库的名称或绝对路径。
 * java remote debug就是使用的JVM TI。
 */
public class AboutJVMTI {
}
