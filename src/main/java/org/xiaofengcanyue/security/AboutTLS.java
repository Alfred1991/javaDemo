package org.xiaofengcanyue.security;

import sun.misc.IOUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 为了保护数据的安全，在涉及网络传输的程序中，可以使用安全套接字层(Secure Sockets Layer,SSL)协议。
 * SSL协议在TCP/IP协议栈中位于传输层协议(TCP)和应用层协议(包括HTTP、Telnet和FTP等）之间。
 * SSL常和HTTP一起使用，用来构建安全的Web应用。
 * 标准化后的SSL协议改名为传输层安全协议(Transport Layer Security,TLS)。
 *
 * SSL的目的在于解决网络传输中存在的三个安全问题。
 *   第一个问题是身份认证，即确保当前正在通信的对等体的身份是合法的。
 *   第二个问题是数据被窃取。
 *   第三个问题是数据可能被篡改。
 * SSL的握手过程比较复杂：
 *   1、客户端发出连接请求。请求包含客户端所能支持的SSL协议的最高版本，以及所能使用的加密算法的信息。
 *   2、服务器端接收到连接请求之后，根据客户端给出的信息，选择双方都能支持的最高版本的SSL协议，并确定双方都能使用的加密算法。
 *     服务端把选择的结果发送给客户端。如果客户端要求认证服务端的身份，那么服务端把它所持有的数字证书发送给客户端。
 *     如果服务端也需要认证客户端的身份，那么服务端向客户端发出认证请求。
 *     如果服务器端发送的数字证书中包含的信息不足以在双反之间进行密钥交换，那么服务端会发送额外的密钥信息。
 *   3、接着由客户端来处理服务端返回的相应内容。
 *     如果客户端收到了服务端进行身份验证的请求，那么客户端使用自己的私钥对所持有的数字证书进行加密之后发送给服务端。
 *     客户端生成在数据传输时进行加密操作所使用的密钥，并使用服务端给出的公钥进行加密之后发送给服务器端。
 *     所有这些操作完成之后，客户端发出通知，切换到加密的数据传输方式。
 *   4、最后由服务端来完成整个握手过程。
 *     如果服务端在第2步中选择验证客户端身份，会先验证客户端发送过来的信息是否正确。接着同样切换到加密的数据传输方式。
 *     握手结束后双方进行数据传输，直到连接关闭。
 *
 * 数字证书中包含证书持有者的身份信息和公钥，它的目的是确保接收者所得到的公钥来自所声明的真是发送者。
 * 证书由用户所信任的机构(Certification Authority)签发，并通过该机构的私钥来加密。数字证书持有者的真实性由信任机构来保证。
 * 在某些情况下，某个证书签发机构的真实性要由另外一个机构的证书来证明，通过这种证明关系可以形成一个证书的链条，而链条的根是公认的值得信赖的机构。
 * 只有当证书链条上的所有证书都被信任时，证书中的所给出的公钥才能得到信任。
 * 支持SSL协议的应用，如浏览器，通常会把一些重要的信任机构的公钥保存起来。
 *
 * SSL握手过程中的一个重要步骤是双方对数据传输时使用的密钥达成一致。
 * 数据传输过程中使用的是对称加密算法。
 * 客户端把生成的密钥经过服务端的公钥加密之后发送给服务端，服务端用自己的私钥解密就得到了双发哦共同使用的密钥。
 *
 * java安全套接字javax.net.ssl.SSLSocket和javax.net.ssl.SSLServerSocket类的使用与普通套接字连接没有太大区别。
 * 使用相应的工厂类javax.net.ssl.SSLSocketFactory和javax.net.ssl.SSLServerSocketFactory进行创建。
 * 在建立连接的过程中，相关的SSL握手机制是自动完成的。
 * 在使用过程中先要创建javax.net.ssl.SSLContext类的对象。SSLContext类采用了标准的服务提供者机制，使用getInstance方法并指定SSL协议的版本可以创建出所需的对象。
 *
 * HTTPS是一种把HTTP和SSL/TLS协议结合起来的通信方式。
 */
public class AboutTLS {

    /**
     * 本例在读物https的内容时会忽略所有与证书和主机名称验证相关的错误，总是能够读出内容。
     */
    public static class ReadAllHttpClient {
        public byte[] read(String urlString) throws IOException, GeneralSecurityException {
            URL url = new URL(urlString);
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(new KeyManager[]{},new TrustManager[]{new MyTrustManager()},new SecureRandom());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(context.getSocketFactory());
            connection.setHostnameVerifier(new MyHostnameVerifier());
            return IOUtils.readFully(connection.getInputStream(),0,true);
        }

        private static class MyTrustManager implements X509TrustManager{

            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }

        private static class MyHostnameVerifier implements HostnameVerifier{
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return false;
            }
        }

    }
}
