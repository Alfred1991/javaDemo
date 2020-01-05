package org.xiaofengcanyue.dynamiccompile;

import javax.tools.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * 使用javac工具的一个问题是其API是Oracle的私有实现。
 * 从Java SE 6开始，Java编译器相关的API以JSR 199的形式规范下来，
 */
public class JavaCompilerAPICompiler {

    public static void main(String[] args) throws Exception {
        Calculator c = new Calculator();
        double dd = c.calculate("(3+2)*5");
        System.out.println(dd);
    }

    public void compile(Path src,Path output) throws IOException{
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try(StandardJavaFileManager fileManager = compiler.getStandardFileManager(null,null,null)){
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(src.toFile());
            Iterable<String> options = Arrays.asList("-d",output.toString());
            JavaCompiler.CompilationTask task = compiler.getTask(null,fileManager,null,options,null,compilationUnits);
            boolean result = task.call();
        }
    }

    /**
     * 以字符串作为源代码进行编译
     */
    public static class StringSourceJavaFileObject extends SimpleJavaFileObject{
        private String content;

        public StringSourceJavaFileObject(String name,String content){
            super(URI.create("string:///"+name.replace('.','/') + Kind.SOURCE.extension),Kind.SOURCE);
            this.content = content;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException{
            return content;
        }
    }

    /**
     * 使用动态编译实现一个计算器
     */
    public static class Calculator extends ClassLoader{
        public double calculate(String expr) throws Exception{
            String className = "CalculatorMain";
            String methodName = "calculate";
            String source = "public class " + className + " { public static double " + methodName + "() { return " + expr +"; } }";
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null,null,null);
            JavaFileObject sourceObject = new StringSourceJavaFileObject(className,source);
            Iterable<? extends JavaFileObject> fileObjects = Arrays.asList(sourceObject);
            Path output = Files.createTempDirectory("calculator").toAbsolutePath();

            System.out.println(output.toAbsolutePath());

            Iterable<String> options = Arrays.asList("-d",output.toString());
            //生成动态编译的字节码文件
            JavaCompiler.CompilationTask task = compiler.getTask(null,fileManager,null,options,null,fileObjects);
            boolean result = task.call();
            if(result){
                byte[] classData = Files.readAllBytes(Paths.get(output.toString(),className+".class"));
                //加载类
                Class<?> clazz = defineClass(className,classData,0,classData.length);
                Method method = clazz.getMethod(methodName);
                Object value = method.invoke(null);
                return (double) value;
            }else{
                throw new Exception("无法识别的表达式。");
            }
        }
    }
}
