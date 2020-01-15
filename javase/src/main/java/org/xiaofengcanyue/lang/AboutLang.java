package org.xiaofengcanyue.lang;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class AboutLang {

    public static void main(String[] args) throws Exception{
//        stringIntern();

//        numberCache();

//        startProcessNormal();

//        dir();

        listProcesses();
    }

    /**
     *  ==是对引用的比较，字符串比较需要使用equals方法。
     *  String类提供了intern方法来返回与当前字符串内容相同但已经被包含在内部缓存中的对象引用（本例中指字面量对象的内部引用），此时可以使用==进行比较。
     *  ==的比较方式效率要高于equals。
     */
    public static void stringIntern(){
        boolean value1 = "Hello" == "Hello";
        boolean value2 = (new String("Hello") == "Hello");
        boolean value3 = (new String("Hello").intern() == "Hello");

        System.out.printf("%s %s %s",value1,value2,value3);
    }

    /**
     * java7把上述的内部化机制扩大到 -128 到 127之间的数字。
     * 对于 -128 到 127范围内的short类型和int类型， 以及\u0000 到 \u007f范围内的char类型，它们对应的包装类对象始终指向相同的对象，即可通过==进行比较。
     * 若希望缓存更多值，可通过虚拟机启动参数java.lang.Integer.IntegerCache.high进行设置。
     */
    public static void numberCache(){
        boolean value1 = Integer.valueOf(3) == Integer.valueOf(3);
        boolean value2 = Integer.valueOf(129) == Integer.valueOf(129);

        System.out.printf("%s %s",value1,value2);
    }


    /**
     * 传统的管道方式启动进程
     * @throws IOException
     */
    public static void startProcessNormal() throws IOException{
        ProcessBuilder pb = new ProcessBuilder("netstat","-a");
        Process process = pb.start();
        InputStream input = process.getInputStream();
        Files.copy(input, Paths.get("netstat.txt"), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * java7增加了两种进程输入与输出方式：
     *  第一种是继承式，新创建的进程的输入与输出与当前的java进程相同。
     *  第二种是基于文件式，把文件作为进程输入与输出的目的地。
     * @throws IOException
     */
    public static void dir() throws IOException{
        ProcessBuilder pb = new ProcessBuilder("pwd");
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.start();
    }

    public static void listProcesses() throws IOException{
        ProcessBuilder pb = new ProcessBuilder("ps","-ef");
        File output = Paths.get("tasks.txt").toFile();
        pb.redirectOutput(output);
        pb.start();
    }


}
