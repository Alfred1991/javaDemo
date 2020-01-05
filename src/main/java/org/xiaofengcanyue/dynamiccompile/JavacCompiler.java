package org.xiaofengcanyue.dynamiccompile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JavacCompiler {

    /**
     * 使用javac是最直接、最简单的动态编译java源代码的方式。
     * @param src
     * @param output
     */
    public void compile(Path src,Path output){
        ProcessBuilder pb = new ProcessBuilder("javac.exe",src.toString(),"-d",output.toString());
        try {
            pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * javac工具提供了编程接口供程序直接调用。
     * @param src
     * @param output
     */
    public void compileByAPI(Path src,Path output){
        String[] args = new String[]{src.toString(),"-d",output.toString()};
        PrintWriter out = null;
        try {
            out = new PrintWriter(Paths.get("output.txt").toFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        com.sun.tools.javac.Main.compile(args,out);
    }
}
