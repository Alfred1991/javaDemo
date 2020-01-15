package org.xiaofengcanyue.dynamiccompile;

import com.sun.xml.internal.ws.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.ProtectionDomain;

public class AboutASM {

    /**
     * 字节代码中的类或接口信息进行访问的访问者ClassVisitor
     */
    public static class MethodCounter implements ClassVisitor{

        public static void main(String[] args) throws IOException {
            ClassReader reader = new ClassReader("java.lang.String");
            MethodCounter counter = new MethodCounter();
            reader.accept(counter,0);
            System.out.println(counter.getCount());
        }

        private int count = 0;

        @Override
        public void visit(int i, int i1, String s, String s1, String s2, String[] strings) {

        }

        @Override
        public void visitSource(String s, String s1) {

        }

        @Override
        public void visitOuterClass(String s, String s1, String s2) {

        }

        @Override
        public AnnotationVisitor visitAnnotation(String s, boolean b) {
            return null;
        }

        @Override
        public void visitAttribute(Attribute attribute) {

        }

        @Override
        public void visitEnd() {

        }

        @Override
        public FieldVisitor visitField(int i, String s, String s1, String s2, Object o) {
            return null;
        }

        @Override
        public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
            count++;
            return null;
        }

        @Override
        public void visitInnerClass(String s, String s1, String s2, int i) {

        }

        public int getCount(){
            return count;
        }
    }


    public static class DrawingComponent extends Component {
        public void paint(Graphics g){
            g.drawLine(30,30,30,100);
            g.drawLine(30,100,70,100);
            g.drawLine(70,100,30,30);

        }
    }

    /**
     * 先使用ASMifierClassVisitor类对DrawingComponent类产生的字节码进行处理，得到基本的使用ASM的Java代码。
     * 再以此Java代码为基础，得到完整的字节代码生成的实现。
     */
    public static class DrawingCodeGenerator implements jdk.internal.org.objectweb.asm.Opcodes{

        public static void main(String[] args) throws IOException {
            DrawingCodeGenerator dcg = new DrawingCodeGenerator();
            dcg.generate("MOVETO 30 30");
        }

        private ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        private MethodVisitor mv = null;
        private int currentX = 0;
        private int currentY = 0;

        public byte[] generate(String sourceCode) throws IOException{
            generateClassInfo();
            generatePaintMethod(sourceCode);
            writer.visitEnd();
            return writer.toByteArray();
        }

        private void generateClassInfo(){
            writer.visit(V1_7,ACC_PUBLIC+ACC_SUPER,"org/xiaofengcanyue/dynamiccompile/DrawingComponent",null,"java/awt/Component",null);
            mv = writer.visitMethod(ACC_PUBLIC,"<init>","()V",null,null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD,0);
            mv.visitMethodInsn(INVOKESPECIAL,"java/awt/Component","<init>","()V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(1,1);
            mv.visitEnd();
        }

        private void generatePaintMethod(String sourceCode) throws IOException{
            mv = writer.visitMethod(ACC_PUBLIC,"paint","(Ljava/awt/Graphics;)V",null,null);
            mv.visitCode();
            BufferedReader reader = new BufferedReader(new StringReader(sourceCode));
            String line = null;
            while((line = reader.readLine()) != null){
                if(line.startsWith("MOVETO")){
                    handleMoveTo(line);
                }else if(line.startsWith("LINETO")){
                    handleLineTo(line);
                }
            }
            mv.visitInsn(RETURN);
            mv.visitMaxs(0,0);
            mv.visitEnd();
        }

        private void handleMoveTo(String line){
            String pos = line.substring("MOVETO ".length());
            String[] parts = pos.split(" ");
            currentX = Integer.parseInt(parts[0]);
            currentY = Integer.parseInt(parts[1]);
        }

        private void handleLineTo(String line){
            String pos = line.substring("LINETO ".length());
            String[] parts = pos.split(" ");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            mv.visitVarInsn(ALOAD,1);
            mv.visitVarInsn(SIPUSH,currentX);
            mv.visitVarInsn(BIPUSH,currentY);
            mv.visitVarInsn(BIPUSH,x);
            mv.visitVarInsn(BIPUSH,y);
            mv.visitMethodInsn(INVOKEVIRTUAL,"java/awt/Graphics","drawLine","(IIII)V");
            currentX = x;
            currentY = y;
        }
    }


    /**
     * 在原始的Java类中添加静态域来跟踪该Java类被创建出来的对象的实例个数
     */
    public static class InstanceCounter extends ClassAdapter implements Opcodes{
        private static class UpdateInstanceCounterAdapter extends MethodAdapter implements Opcodes{
            private String className;
            public UpdateInstanceCounterAdapter(String className,MethodVisitor mv){
                super(mv);
                this.className = className;
            }
            public void visitInsn(int opcode){
                if((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW){
                    mv.visitFieldInsn(GETSTATIC,className,"instanceCount","I");
                    mv.visitInsn(ICONST_1);
                    mv.visitInsn(IADD);
                    mv.visitFieldInsn(PUTSTATIC,className,"instanceCount","I");
                }
                mv.visitInsn(opcode);
            }
            public void visitMaxs(int maxStack,int maxLocals){
                mv.visitMaxs(maxStack + 2,maxLocals);
            }
        }
        private String className;

        public InstanceCounter(ClassVisitor cv){
            super(cv);
        }

        public void visit(int version,int access,String name,String signature,String superName,String[] interfaces){
            cv.visit(version,access,name,signature,superName,interfaces);
            className = name;
            FieldVisitor fv = cv.visitField(ACC_PUBLIC+ACC_STATIC,"instanceCount","I",null,null);
            fv.visitEnd();
        }

        public MethodVisitor visitMethod(int access,String name,String desc,String signature,String[] exceptions){
            MethodVisitor mv = cv.visitMethod(access,name,desc,signature,exceptions);
            if("<init>".equals(name)){
                mv = new UpdateInstanceCounterAdapter(className,mv);
            }
            return mv;
        }

        public static void main(String[] args) throws IOException{
            ClassReader reader = new ClassReader("org.xiaofengcanyue.dynamiccompile.AboutASM");
            ClassWriter writer = new ClassWriter(0);
            InstanceCounter counter = new InstanceCounter(writer);
            reader.accept(counter,0);
            byte[] byteCode = writer.toByteArray();
            Files.write(Paths.get("bin","com"),byteCode);

        }
    }

    /**
     * asm的树形api
     */
    public static class TreeMethodCounter{
        public int count(String className) throws IOException{
            jdk.internal.org.objectweb.asm.ClassReader reader = new jdk.internal.org.objectweb.asm.ClassReader(className);
            ClassNode cn = new ClassNode();
            reader.accept(cn,0);
            return cn.methods!=null?cn.methods.size():0;
        }

        public static void main(String[] args) throws IOException {
            TreeMethodCounter counter = new TreeMethodCounter();
            int count = counter.count("java.lang.String");
            System.out.println(count);
        }
    }

    /**
     * 对字节代码的转换操作由特殊的代理程序完成。不同虚拟机实现提供的启动代理的方式不尽相同，一般有两种。
     *  一种是通过启动参数指定jar包路径（-javaagent），它要求代理程序的jar包的清单文件中要包含Premain-Class属性。
     *  第二种做法是在虚拟机运行主程序后再启动代理程序，这类代理程序的jar包的清单文件中要由Agent-Class属性指明代理类的名称
     */
    public static class TraceTransformer implements ClassFileTransformer{
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            ClassReader reader = new ClassReader(classfileBuffer);
            ClassWriter writer = new ClassWriter(0);
            ClassAdapter adapter= new ClassAdapter(writer){
                @Override
                public MethodVisitor visitMethod(int access, final String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor mv = cv.visitMethod(access,name,desc,signature,exceptions);
                    return new MethodAdapter(mv){
                        @Override
                        public void visitCode() {
                            mv.visitCode();
                            mv.visitFieldInsn(Opcodes.GETSTATIC,"java/lang/System","out","Ljava/io/PrintStream;");
                            mv.visitLdcInsn("进入方法："+name);
                            mv.visitMethodInsn(jdk.internal.org.objectweb.asm.Opcodes.INVOKEVIRTUAL,"java/io/PrintStream","println","(Ljava/lang/String;)V");
                        }

                        @Override
                        public void visitMaxs(int maxStack, int maxLocals) {
                            mv.visitMaxs(maxStack+2,maxLocals);
                        }
                    };
                }
            };
            reader.accept(adapter,0);
            return writer.toByteArray();
        }
    }
}
