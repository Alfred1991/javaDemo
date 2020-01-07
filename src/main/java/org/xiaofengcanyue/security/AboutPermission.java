package org.xiaofengcanyue.security;

import sun.security.util.SecurityConstants;

import javax.security.auth.Subject;
import java.io.FilePermission;
import java.io.IOError;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;

/**
 * 访问控制中两个重要的概念是权限和策略。
 *   权限用来表示允许执行某些操作的能力；策略用来说明权限的分配方式。
 * 如果在策略中为某个主体分配了某个权限，那么该主体可以执行由该权限对应的操作。
 */
public class AboutPermission {
    /**
     * java安全中的权限由java.security.Permission类及其子类来表示。
     * java安全中的策略由java.security.Policy类表示。Policy类的对象的使用方式类似于登录过程中使用的Configuration类的对象。
     *   同一时间内，只能有一个Policy类的对象处于活动状态。与权限相关的决策都通过当前获得的Policy类的对象来处理。
     *   jdk中提供了policytool工具帮助编辑策略文件。
     * 保护域是一个主体所能访问的对象的集合。程序通过策略中的声明把不同的权限分配给不同的主体，这些权限定义了主体所能访问的对象的集合。
     *   一般讲java平台分成两类保护域：
     *     1、系统域用来保护系统中的资源，用来保护系统中的文件、网络连接、屏幕显示、键盘鼠标等资源。
     *     2、应用域，由应用根据需要具体划分。
     *   每个保护域中包含一组java类、主体的身份标识和权限列表。
     *   类java.security.ProtectionDomain表示保护域。
     *
     * 在声明了程序在运行中应该满足的访问控制权限之后，需要在代码中显示地应用这些要求。
     * 访问控制权限的检查由java.lang.SecurityManager类（早期的实现方式）和java.security.AccessController类来共同完成
     */
    public void writeFile(Path path,byte[] content) throws IOException{
        SecurityManager securityManager = System.getSecurityManager();
        if(securityManager != null){
            securityManager.checkWrite(path.toString());
        }
        Files.write(path,content);
    }

    public void writeFile1(Path path,byte[] content) throws IOException{
        FilePermission permission = new FilePermission(path.toString(), SecurityConstants.FILE_WRITE_ACTION);
        AccessController.checkPermission(permission);
        Files.write(path,content);
    }

    /**
     * 在进行权限检查时，不仅需要查看对该方法的调用在当前调用上下文中是否合法，还需要沿着方法调用栈逐个向上检查，确保对其中每个方法的调用都是合法的。
     * 这些方法的调用过程形成一个完整的链条，如果其中任何一个方法不具有所需的权限，那么整个调用过程是非法的并抛出AccessControlException异常。
     * 唯一例外的是特权动作(privilegd action)，特权动作只关心是否具备动作本身所要求的权限。
     * 在沿着调用链向上进行权限检查的过程中，如果遇到了特权动作，则只检查该特权动作所要求的权限是否满足，而不再继续沿着调用链向上检查。
     *
     * 本例使用PrivilegedAction接口实现特权动作。
     * 只要为getWithPrivilege方法添加所需的"java.util.PropertyPermission"权限声明，程序中的其他部分即可直接调用该方法。
     */
    public static String getWithPrivilege(final String property){
        return AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty(property);
            }
        });
    }

    /**
     * 如果特权动作的执行抛出了受检异常，那么需要使用PrivilegedExceptionAction接口的实现类。
     */
    public void writeFileWithPrivilege(final Path path,final byte[] content) throws IOException{
        try{
            AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    Files.write(path,content);
                    return path.toString();
                }
            });
        }catch (PrivilegedActionException e){
            throw (IOException) e.getCause();
        }
    }

    /**
     * 在一个线程的执行过程中，该线程维护了与它所具有的访问控制权限相关的上下文信息。
     * 当前线程在新线程创建时的访问控制上下文信息会被保存下来，并作为被继承的上下文信息与新创建的线程关联起来。
     * 在调用方法时的访问控制权限检查会先按照正常的方式检查当前线程中方法的调用栈，再使用继承的上下文信息来进行检查。
     * 访问控制上下文由java.security.AccessControlContext类表示，通过AccessController类的getContext方法可以得到当前线程所使用的的访问控制上下文信息的一个快照。
     * 每个AccessControlContext类的对象是与一组ProtectionDomain类的对象关联在一起的。
     * 在AccessControlContext类的checkPermission方法的实现中，作为参数传递的Permission类的对象会被传递给所有关联的ProtectionDomain类的对象的implies方法。
     * 只有当所有的ProtectionDomain类的对象调用implies方法的返回值都为true时，需要检查的权限才是具备的。
     */
    public void doAs(){
        Subject subject = new Subject();
        AboutAuthentication.UserPrincipal principal = new AboutAuthentication.UserPrincipal("Alex");
        subject.getPrincipals().add(principal);
        subject.setReadOnly();

        String userHome = Subject.doAsPrivileged(subject, new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("user.home");
            }
        },null);
        /**
         * 策略文件：
         * grant principal org.xiaofengcanyue.security.AboutAuthentication.UserPrincipal "Alex" {
         *   permission java.util.PropertyPermission "user.*","read";
         *   };
         * grant {
         *   permission javax.security.auth.AuthPermission "*";
         *   };
         */
    }

    /**
     * 通过传递AccessControlContext类的对象可以在其他线程中通过当前线程的访问控制上下文信息进行权限检查。
     * 在有些情况下，可能传递AccessControlContext类的对象不是一个可以接受的选择，这时可使用java.security.GuardedObject类和java.security.Guard接口。
     */
    public GuardedObject readFile(Path path) throws IOException{
        FilePermission permission = new FilePermission(path.toString(),SecurityConstants.FILE_READ_ACTION);
        byte[] data = Files.readAllBytes(path);
        GuardedObject guardedObj = new GuardedObject(data,permission);
        return guardedObj;
    }
    public void useFile(Path path) throws IOException{
        GuardedObject guardedObj = readFile(path);
        byte[] data = (byte[]) guardedObj.getObject();
    }

}
