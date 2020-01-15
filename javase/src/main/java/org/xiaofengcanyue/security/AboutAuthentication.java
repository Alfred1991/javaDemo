package org.xiaofengcanyue.security;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.*;
import javax.security.auth.spi.LoginModule;
import java.io.*;
import java.security.Principal;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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

    /**
     * 对于login方法，返回true表明该登录模块所执行的身份认证操作是成功的。如果返回false，说明该模块在当前登录过程中被忽略。
     * 在commit方法中，认证成功后会把相关的身份标识和凭证信息关联到Subjet类的对象上。
     *   若login成功，而commit出现错误，那么应该抛出LoginException异常；若没有出现错误则返回true。
     *   若login失败，则commit直接返回false。
     * 在abort方法中，如果认证成功，则不管commit方法是否成功，只要abort方法本身执行时没有出现错误，abort方法总是返回ture；
     *   如果abort方法本身出现错误，那么抛出LoginException异常。
     *   如果认证失败，则不管commit和abort方法是否成功完成，abort方法都直接返回false。
     * 在logout方法中，如果注销成功返回true，否则抛出LoginException异常。
     */
    public static class PropertiesFileBaseLoginModule implements LoginModule{
        private CallbackHandler callbackHandler;
        private Subject subject;
        private Properties props = new Properties();
        private boolean authSucceeded = false;
        private String authUsername = null;

        @Override
        public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
            this.callbackHandler = callbackHandler;
            this.subject = subject;
            String propsFilePath = (String) options.get("properties.file.path");
            File propsFile = new File(propsFilePath);
            try{
                props.load(new FileInputStream(propsFile));
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        @Override
        public boolean login() throws LoginException {
            TextInputCallback usernameInputCallback = new TextInputCallback("用户名：");
            TextInputCallback passwordInputCallback = new TextInputCallback("密码：");
            try{
                callbackHandler.handle(new Callback[]{usernameInputCallback,passwordInputCallback});
            }catch (Exception e){
                throw new LoginException(e.getMessage());
            }
            String username = usernameInputCallback.getText();
            if(username == null || "".equals(username.trim())){
                throw new AccountException("用户名为空！");
            }
            if(!props.containsKey(username)){
                throw new AccountNotFoundException("该用户不存在！");
            }
            String password = passwordInputCallback.getText();
            if(password == null || "".equals(password.trim())){
                throw new CredentialException("密码为空！");
            }
            if(!password.equals(props.get(username))){
                throw new FailedLoginException("用户名和密码不匹配！");
            }
            authSucceeded = true;
            authUsername = username;
            return true;
        }

        @Override
        public boolean commit() throws LoginException {
            if(authSucceeded){
                this.subject.getPrincipals().add(new UserPrincipal(authUsername));
                authUsername = null;
                authSucceeded = false;
                return true;
            }
            return false;
        }

        @Override
        public boolean abort() throws LoginException {
            authUsername = null;
            if(authSucceeded){
                authSucceeded = false;
                return true;
            }
            return false;
        }

        @Override
        public boolean logout() throws LoginException {
            Set<Principal> principals = subject.getPrincipals();
            Set<UserPrincipal> userPrincipals = subject.getPrincipals(UserPrincipal.class);
            for(UserPrincipal principal:userPrincipals){
                if(principal.getName().equals(authUsername)){
                    principals.remove(principal);
                    break;
                }
            }
            return true;
        }
    }

    /**
     * 在一个Configuration类的对象中，一般同时维护多个配置。每个配置都有自己的名称，由一组javax.security.auth.login.AppConfigurationEntry类的对象组成。
     * 每个AppConfigurationEntry类的对象表示一个登录模块配置项。每个配置项由三部分组成，分别是登录模块的java类、登录模块的控制标记和额外的配置参数。
     * 配置中的每个登录模块会按照对应配置项出现的顺序依次执行。
     * 控制标记用来说明每个登陆模块在整个登录过程中的作用，有必须、必要、充分和可选4中标记。
     *   "必须（Required）"的登录模块被要求认证成功，但无论认证成功与否，在其后出现的登录模块仍然会被执行。
     *   "必要（Requisite）"的模块，如果认证成功，那么在其后出现的登录模块会被执行；如果认真失败，则整个登录过程直接失败。
     *   "充分（Sufficient）"的登录模块，如果认证成功，那么整个登录过程直接变为成功状态，后面的登录模块不会被执行；如果认证失败，则继续执行后面的登录模块。
     *   "可选（Optional）"的登录模块，不管登录成功与否，后面的模块仍然会被执行。
     * 只有当所有声明为"必须"和"必要"的登录模块都认证成功时，整个登录过程才是成功的。
     * 如果声明为"充分"的登录模块认证成功，那么只要求出现在该模块之前的"必须"和"必要"模块认证成功即可。
     * 如果没有配置"必须"或"必要"的登录模块，则至少要有一个声明为"充分"或"可选"的登录模块认证成功，整个登录过程才是成功的。
     *
     * 运行时只有一个Configuration类的对象起作用。
     * 通过Configuration类的静态方法getConfiguration可以获取这个类的对象。
     * 如果使用默认的配置方式，那么需要添加虚拟机启动参数"java.security.auth.login.config"来指定配置文件的路径。
     */
    public static class MyApp{
         private LoginContext loginContext;
         public MyApp() throws LoginException{
             TextCallbackHandler callbackHandler = new TextCallbackHandler();
             loginContext = new LoginContext("MyApp",callbackHandler);
         }
         public Subject login() throws LoginException{
             loginContext.login();
             return loginContext.getSubject();
         }
         public void logout() throws LoginException{
             loginContext.logout();
         }

        /**
         * 需带启动参数 -Djava.security.auth.login.config=target/classes/org/xiaofengcanyue/security/MyApp
         */
        public static void main(String[] args) throws Exception{
            MyApp app = new MyApp();
            Subject subject = app.login();
            for(Principal p :subject.getPrincipals()){
                System.out.println(p.getName());
            }
        }
    }


}
