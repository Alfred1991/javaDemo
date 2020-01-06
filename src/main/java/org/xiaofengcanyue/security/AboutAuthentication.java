package org.xiaofengcanyue.security;

import javax.security.auth.callback.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Principal;

public class AboutAuthentication {
    /**
     * 在java安全中用术语"主体(subject)"来表示访问请求的来源，通常的理解是把主体看成系统的用户。
     * 一个主体可以有多个不同的身份标识(principal)。身份标识是主体的具体表示，最常见的身份标识就是用户名。
     * 有的系统可以由多个身份标识，例如用户名、手机号码等等。
     * 除了身份标识之外，一个主体还可以有公开或私有的安全相关的凭证(credential)，包括密码和密钥等。
     *
     * java api中表示主体的是javax.security.auth.Subject类，表示用户身份标识的是java.security.Principal接口。
     * 一个主体可与多个身份标识关联，主体所关联的凭证信息分成公开和私有两类。
     *
     * 本例实现了一个用户名的身份标识。
     * 为防止程序中的代码对Subject类对象进行错误的修改，可以在添加必要的身份标识和凭证信息之后，调用setReadOnly方法将其设为只读。
     */
    public static class UserPrincipal implements Principal{
        private final String username;
        public UserPrincipal(String username){
            this.username = username;
        }
        public String getName(){
            return username;
        }
    }

    /**
     * 典型的用户认证过程通过登录操作来完成。
     * 用户把持有的身份标识和凭证信息提供给系统，由系统进行相关的身份验证操作。
     * java提供了一个可扩展的登录框架，它由登录上下文、登录模块和登录配置等几个部分组成。
     * 登录相关的api在javax.security.auth.login包中，整个登录过程由javax.security.auth.login.LoginContext类的对象来负责启动和管理。
     * 一个LoginContext类的对象一般只用来对一个Subject类的对象进行认证，认证成功后程序的其他部分可通过查询该Subject类的对象是否存在来判断当前用户是否通过了认证。
     * 对于认证之后的Subject类的对象，桌面程序通常把它作为全局对象，Web应用通常使用ThreadLocal类来对它进行封装。
     * 具体的登录方式由javax.security.auth.spi.LoginModule接口的实现类来实现。
     * 在一个登录过程中可能使用多种不同的登录方式，例如可使用基于用户名和密码的登录方式，也可以使用动态手机验证码作为登录方式。开发者通过实现LoginModule接口来封装不同的登录逻辑。
     * 对于程序中使用的登录方式，可以在不修改代码的情况下通过修改配置进行更新。登录方式的配置由javax.security.auth.login.Configuration类的对象来表示。
     * 在登录过程中可能需要与用户或其他组件进行交互，可以通过javax.security.auth.callback.CallbackHandler接口的实现类来表示。
     */
    public static class TextCallbackHandler implements CallbackHandler{
        @Override
        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for(Callback callback : callbacks){
                if(callback instanceof TextInputCallback){
                    TextInputCallback textInputCallback = (TextInputCallback) callback;
                    System.out.print(textInputCallback.getPrompt());
                    textInputCallback.setText(new BufferedReader(new InputStreamReader(System.in)).readLine());
                }else if(callback instanceof TextOutputCallback){
                    TextOutputCallback textOutputCallback = (TextOutputCallback) callback;
                    String messageType = "";
                    switch (textOutputCallback.getMessageType()){
                        case TextOutputCallback.INFORMATION:
                            messageType="信息：";
                            break;
                        case TextOutputCallback.WARNING:
                            messageType = "警告：";
                            break;
                        case TextOutputCallback.ERROR:
                            messageType = "错误：";
                            break;
                    }
                    System.out.println(messageType + textOutputCallback.getMessage());
                }else{
                    throw new UnsupportedCallbackException(callback);
                }
            }
        }
    }
}
