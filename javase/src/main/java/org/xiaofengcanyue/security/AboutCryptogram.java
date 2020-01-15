package org.xiaofengcanyue.security;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
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

    /**
     * 报文摘要使用某种算法对原始数据进行处理，得到一个固定长度的摘要，这个摘要会随着数据一同公开出来。
     * 接收者对接收到的数据使用相同的算法计算出摘要，再与正确的摘要进行比较。
     * 如果不一致，则说明数据已经被篡改。
     * 进行报文摘要的MessageDigest类的使用方式类似于之前的Cihper类。
     */
    public void useOfMessageDigest() throws Exception{
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest("Hello World".getBytes());
    }

    /**
     * 与报文摘要类似的机制是消息验证码（Message Authentication Code,MAC）。
     * 它与报文摘要的不同之处在于消息验证码在计算过程中使用了密钥，只有掌握了密钥的接收者才能验证数据的完整性。
     * 它解决了摘要本身也会被篡改的问题。
     */
    public void useOfMAC() throws Exception{
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacMD5");
        SecretKey key = keyGenerator.generateKey();
        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(key);
        byte[] result = mac.doFinal("Hello World".getBytes());
    }

    /**
     * 数字签名可以用来实现身份验证功能，它需要使用一对公钥和私钥。
     * 例如，对于进行通信的两个对等体A和B，如果A需要验证B的身份，那么B需要使用私钥对消息进行加密，并把加密结果发送给A，A使用公钥进行解密。
     * 由于私钥只有B知道，当A使用公钥成功对数据进行解密之后，可以判定消息的来源肯定是该公钥对应的持有者B，这就相当于B对消息进行了签名。
     * 数字签名服务由java.security.Signature类来提供。
     */
    public static class DigitalSignature{
        private Signature signature;
        private PublicKey publicKey;
        private PrivateKey privateKey;
        private byte[] data = "Hello World".getBytes();
        public DigitalSignature(){
            init();
        }
        private void init(){
            try{
                signature = Signature.getInstance("SHA1withDSA");
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
                KeyPair keyPair = keyPairGenerator.generateKeyPair();
                publicKey = keyPair.getPublic();
                privateKey = keyPair.getPrivate();
            }catch (GeneralSecurityException e){
                e.printStackTrace();
            }
        }

        public byte[] sign() throws GeneralSecurityException{
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        }

        public boolean verify(byte[] signatureData) throws GeneralSecurityException{
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(signatureData);
        }

        public void testSignature() throws GeneralSecurityException{
            boolean result = verify(sign());
            System.out.println(result);
        }

        public static void main(String[] args) throws GeneralSecurityException {
            new DigitalSignature().testSignature();
        }
    }

    /**
     * 当需要对某个java对象进行签名时，可以使用java.security.SignedObject类。
     * 它可以用来封装任何实现了Serializable接口的类的对象。
     * 它类似于SealedObject类。
     */
    public void saveObject(Serializable obj,Path path) throws GeneralSecurityException,IOException{
        Signature signature = Signature.getInstance("SHA1withDSA");
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        SignedObject signedObj = new SignedObject(obj,keyPair.getPrivate(),signature);
        try(ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))){
            oos.writeObject(signedObj);
        }
    }

}
