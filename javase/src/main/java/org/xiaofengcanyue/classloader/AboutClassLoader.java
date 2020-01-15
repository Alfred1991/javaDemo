package org.xiaofengcanyue.classloader;

import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Properties;

/**
 * java平台上的类加载器可分为两类：启动类加载器（由原生代码实现）和用户自定义类加载器（继承自ClassLoader类）。
 *
 * java平台默认提供的自定义类加载器有两个：扩展类加载器（extension class loader，根据特定的路径加载类）和系统类加载器（system class loader，根据classpath加载类）。
 * 使用类加载器对象的defineClass方法从字节代码中定义出表示Java类的Class类对象，若某个java类是由某个类加载器对象的defineClass方法定义的，则称这个类加载器对象是该java类的定义类加载器（defining class loader）。
 * 当使用类加载器的loadClass方法来加载一个java类时，称这个类加载器对象是该java类的初始类加载器（initiating class loader）。
 * 一个java类的初始类加载器 和 定义类加载器 不一定相同。一个java类的定义类加载器 是该类 所引用的其他java类的初始类加载器。
 */
public class AboutClassLoader {

    public static void main(String[] args) throws Exception{
//        loadClass();

//        displayParents();

        new NoParentClassLoader().testLoad();
    }

    public static void loadClass()throws Exception{
        ClassLoader current = AboutClassLoader.class.getClassLoader();
        Class<?> clazz = current.loadClass("java.lang.String");
        Object str = clazz.newInstance();
        System.out.println(str.getClass());
    }

    /**
     * java平台上默认提供的类加载形成，从根节点开始依次是启动类加载器->扩展类加载器->系统类加载器。
     * 在classloader类的默认实现中，当类加载器对象需要加载一个java类或资源时，会先把加载请求代理给双亲类加载器。只有在双亲类加载器对象无法找到java类或资源时，才由当前类加载器对象进行处理。
     */
    public static void displayParents(){
        ClassLoader current = AboutClassLoader.class.getClassLoader();
        while(current != null){
            System.out.println(current.toString());
            current = current.getParent();
        }
    }

    /**
     * 由于没有双亲类加载器可以代理加载类的请求，因此加载过程失败。
     */
    public static class NoParentClassLoader extends ClassLoader{
        public NoParentClassLoader(){
            super(null);
        }

        public void testLoad() throws ClassNotFoundException{
            Class<?> clazz = loadClass("org.xiaofengcanyue.classloader.AboutClassLoader");
        }
    }

    /**
     * 创建自定义类加载的原因：
     *  1、对java类的字节代码进行特殊的查找和处理，如java字节代码存放在特定位置或远程服务器上，或者字节代码的数据经过了加密处理；
     *  2、利用类加载器产生的隔离特性来满足特殊的需求。
     */
    public static class FileSystemClassLoader extends ClassLoader{

        private Path path;
        public FileSystemClassLoader(Path path){
            super(null);
            this.path = path;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            try{
                byte[] classData = getClassData(name);
                return defineClass(name,classData,0,classData.length);
            }catch (IOException e){
                throw new ClassNotFoundException();
            }
        }

        private byte[] getClassData(String className) throws IOException{
            Path classFilePath = classNameToPath(className);
            System.out.println(classFilePath.toAbsolutePath());
            return Files.readAllBytes(classFilePath);
        }

        private Path classNameToPath(String className){
            return path.resolve(className.replace('.', File.separatorChar) + ".class");
        }
    }

    /**
     * 动态生成字节代码的类加载器。
     */
    public static class GreetingClassLoader extends ClassLoader implements Opcodes{
        private String message;
        public GreetingClassLoader(String message){
            this.message = message;
        }
        protected Class<?> findClass(String name) throws ClassNotFoundException{
            byte[] classData = generateClassData(name);
            return defineClass(name,classData,0,classData.length);
        }

        private byte[] generateClassData(String className){
            className = className.replace("\\.","/");
            ClassWriter writer = new ClassWriter(0);
            writer.visit(V1_7,ACC_PUBLIC + ACC_SUPER,className,null,"java/lang/Object",null);
            MethodVisitor mv = writer.visitMethod(Opcodes.ACC_PUBLIC,"<init>","()V",null,null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD,0);
            mv.visitMethodInsn(INVOKESPECIAL,"java/lang/Object","<init>","()V");
            mv.visitFieldInsn(GETSTATIC,"java/lang/System","out","Ljava/io/PrintStream;");
            mv.visitLdcInsn(message);
            mv.visitMethodInsn(INVOKEVIRTUAL,"java/io/PrintStream","println","(Ljava/lang/String;)V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2,1);
            mv.visitEnd();
            writer.visitEnd();
            return writer.toByteArray();
        }
    }

    /**
     * 复写loadclass方法，改变默认的双亲优先的代理模式
     */
    public static class ParentLastClassLoader extends ClassLoader{
        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            Class<?> clazz = findLoadedClass(name);
            if(clazz != null){
                return clazz;
            }
            clazz = findClass(name);
            if(clazz != null){
                return clazz;
            }
            ClassLoader parent = getParent();
            if(parent!=null){
                return parent.loadClass(name);
            }else{
                return super.loadClass(name,resolve);
            }
        }
    }

    /**
     * 类加载器为它所加载的java类创建了隔离空间：
     *  相同的字节代码 如果由不同的类加载器对象来加载并定义，所得到的的Class类的对象是不相等的。
     */
    public static class ClassIdentity{

        public static void main(String[] args){
            try {
                test();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public static void test() throws Exception{
            Path path = Paths.get("target/classes").toAbsolutePath();
            System.out.println(path);

            FileSystemClassLoader fscl1 = new FileSystemClassLoader(path);
            FileSystemClassLoader fscl2 = new FileSystemClassLoader(path);
            String className = "org.xiaofengcanyue.classloader.Sample";
            Class<?> class1 = fscl1.loadClass(className);
            System.out.println(class1.getName()+"-"+class1.getClassLoader());
            Object obj1 = class1.newInstance();
            Class<?> class2 = fscl2.loadClass(className);
            System.out.println(class2.getName()+"-"+class2.getClassLoader());
            Object obj2 = class2.newInstance();
            Method setSampleMethod = class1.getMethod("setSample",java.lang.Object.class);
            System.out.println(setSampleMethod.getName());
            setSampleMethod.invoke(obj1,obj2);
            System.out.println("hehe");
        }
    }

    /**
     * 使用类加载器加载资源文件
     */
    public static class LoadResource{

        public static void main(String[] args) throws IOException {
            Properties p = loadConfig();
            System.out.println(new String(p.getProperty("GREETING").getBytes("ISO8859-1"), Charset.forName("utf-8")));
        }

        public static Properties loadConfig() throws IOException{
            ClassLoader loader = AboutClassLoader.class.getClassLoader();

            URL url = loader.getResource("org/xiaofengcanyue/localandi18n/Messages_zh_CN.properties");

            System.out.println(url.toString());

            Enumeration<URL> e = loader.getResources("Messages_zh_CN.properties");

            while(e.hasMoreElements()){
                url = e.nextElement();
                System.out.println(url.toString());
            }


            InputStream input = loader.getResourceAsStream("org/xiaofengcanyue/localandi18n/Messages_zh_CN.properties");
            if(input == null){
                throw new IOException("找不到配置文件");
            }
            Properties props = new Properties();
            props.load(input);
            return props;
        }
    }

    /**
     * Tomcat中的web应用对应的类加载器是org.apache.catalina.loader.WebappClassLoader类的对象，它继承自标准库的java.net.URLClassLoader类。
     * WebappClassLoader类中的loadClass方法的实现按照下面步骤进行加载：
     *  1、调用findLoadedClass查看是否已经被加载过。
     *  2、调用系统类加载器的loadClass方法来尝试加载类。
     *  3、在满足下面两种情况时会代理给双亲加载器：
     *      1、setDeletegate方法打开代理模式之后。
     *      2、类的名称满足一定的条件，比如以"javax.servlet"开头。
     *  4、调用findClass方法来查找Web应用本身的Java类。
     *  5、若在步骤3中没有代理给双亲类，则在这一步中进行。
     * WebappClassLoader的findClass方法的查找过程如下：
     *  1、若存在外部仓库（对应一个URL类的对象，表示一个加载类时的查找路径）且配置了优先查找外部仓库，则调用双亲类加载器对象的findClass方法进行查找。
     *  2、在内部仓库（WEB-INF下的classes和lib）中进行查找。
     *  3、若存在外部仓库且配置了不优先查找外部仓库，则此时调用双亲类加载器对象的findClass方法进行查找。
     */

    /**
     * OSGi是java平台上的动态模块系统，它提供了面向服务和基于组件的运行环境，并提供标准的方式来管理软件的生命周期。
     * OSGi中最基本的组成部分是模块（bundle），由java类和所需的资源文件组成，以jar包的形式出现。
     * 每个谋爱的java类相当于存在于一个受管理的隔离空间中，对该空间的管理由OSGi的类加载器完成。
     * OSGi中每个模块使用清单文件声明自己所依赖的所需要导入的来自其他模块的java包名称 和 自己提供出来的可供其他模块使用的java包的名称。
     *
     * 目前存在不少OSGi规范的实现，这里使用Eclipse Equinox，运行环境在Eclipse IDE中。
     * 每个模块在运行时都有一个对应的类加载器对象。这样一个模块中只有通过Export-Package属性声明的java包才对外可见（会代理给该包对应的类加载器进行加载）。
     *
     */
}
