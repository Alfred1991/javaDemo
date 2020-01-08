package org.xiaofengcanyue.security;

import com.sun.org.apache.bcel.internal.generic.ATHROW;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Provider;
import java.security.Security;
import java.util.Set;

public class AboutCryptogram {

    public static void main(String[] args) {
        printProviders();
    }

    /**
     * 对于不同类别的服务，java标准库提供了对应的java类作为该服务的抽象表示。
     * 以报文摘要为例，java.security.MessageDigest类是该服务的抽象表示。
     * 这些抽象类一般都提供一个getInstance方法根据算法名称和提供者的名称来创建出具体的实现对象。
     * 打印所有的服务
     */
    public static void printProviders(){
        Provider[] providers = Security.getProviders();
        for(Provider provider : providers){
            Set<Provider.Service> services = provider.getServices();
            for(Provider.Service service : services){
                System.out.println( provider.getName() + ":" +service.getAlgorithm() + " <==> " + service.getClassName());
            }
        }
    }

    /**
     * 密钥在java密码框架中有两种表示方式：
     *   一种是基于java.security.Key接口的不透明表示方式。
     *   另一种是基于java.security.spec.KeySpec接口的透明表示方式。
     * 对于对称算法来说，javax.crypto.SecretKey接口表示唯一的私钥；对于非对称算法来说，java.security.PublicKey接口和java.security.Private接口分表表示公钥和私钥。
     * 密钥的获取通过标准的服务提供者来完成，对称算法使用javax.crypto.KeyGenerator类，非对称算法使用java.security.KeyPairGenerator类。
     * 加密和解密功能由服务javax.crypto.Cipher类提供。
     */
    public static class SymmetricEncryption{
        public void encrypt() throws Exception{
            KeyGenerator generator = KeyGenerator.getInstance("DES");
            SecretKey key = generator.generateKey();
            Files.write(Paths.get("key.data"),key.getEncoded());
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE,key);
            String text = "Hello World";
            byte[] encrypted = cipher.doFinal(text.getBytes());
            Files.write(Paths.get("encrypted.bin"),encrypted);
        }
        public void decrypt() throws Exception{
            byte[] keyData = Files.readAllBytes(Paths.get("key.data"));
            SecretKeySpec keySpec = new SecretKeySpec(keyData,"DES");
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE,keySpec);
            byte[] data = Files.readAllBytes(Paths.get("encrypted.bin"));
            byte[] result = cipher.doFinal(data);
            System.out.println(new String(result));
        }

        public static void main(String[] args) throws Exception {
            SymmetricEncryption s = new SymmetricEncryption();
            s.encrypt();
            s.decrypt();
        }
    }

    /**
     * 对一个数据流进行加密、解密操作可以使用javax.crypto.CipherInputStream类和javax.crypto.CipherOutputStream类。
     */
    public void storeSafely(Serializable obj, Cipher cipher, Path path) throws IOException{
        try(ObjectOutputStream oos = new ObjectOutputStream(new CipherOutputStream(Files.newOutputStream(path),cipher))){
            oos.writeObject(obj);
        }
    }

}
