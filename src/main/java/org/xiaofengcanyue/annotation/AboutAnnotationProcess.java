package org.xiaofengcanyue.annotation;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class AboutAnnotationProcess {

    /**
     * JSR 269的注解处理机制的重要特征是采用可插拔的设计方式。底层提供基本框架和运行环境，开放人员编写注解处理功能作为插件嵌入到此框架之中。对于注解的处理分多轮进行。
     */
    @SupportedSourceVersion(SourceVersion.RELEASE_7)
    @SupportedAnnotationTypes("org.xiaofengcanyue.annotation.Author")
    public static class AuthorProcessor extends AbstractProcessor{
        private Map<String,Integer> countMap = new HashMap<>();
        private TypeElement authorElement;

        @Override
        public synchronized void init(ProcessingEnvironment processingEnv) {
            super.init(processingEnv);
            Elements elementUtils = processingEnv.getElementUtils();
            authorElement = elementUtils.getTypeElement("org.xiaofengcanyue.annotation.Author");
        }

        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(authorElement);
            for(Element element:elements){
                processAuthor(element);
            }
            if(roundEnv.processingOver()){
                for(Map.Entry<String,Integer> entry:countMap.entrySet()){
                    System.out.println(entry.getKey() + " ===> "+entry.getValue());
                }
            }
            return true;
        }

        private void processAuthor(Element element){
            List<? extends AnnotationMirror> annotations = element.getAnnotationMirrors();
            for(AnnotationMirror mirror:annotations){
                String name = (String) getAnnotationValue(mirror,"name");
                if(!countMap.containsKey(name)){
                    countMap.put(name,1);
                }else {
                    countMap.put(name,countMap.get(name) + 1);
                }
            }
        }

        private Object getAnnotationValue(AnnotationMirror mirror,String name){
            Map<? extends ExecutableElement,? extends AnnotationValue> values = mirror.getElementValues();
            for(Map.Entry<? extends ExecutableElement,? extends AnnotationValue> entry:values.entrySet()){
                ExecutableElement elem = entry.getKey();
                AnnotationValue value = entry.getValue();
                String elemName = elem.getSimpleName().toString();
                if(name.equals(elemName)){
                    return value.getValue();
                }
            }
            return null;
        }
    }

    /**
     * 运行中以反射方式使用注解
     */
    public static class EmployeeInfoManagerFactory{
        private static class AccessInvocationHandler<T> implements InvocationHandler{
            private final T targetObject;
            public AccessInvocationHandler(T targetObject){
                this.targetObject = targetObject;
            }

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Role annotation = method.getAnnotation(Role.class);
                if(annotation != null){
                    String[] roles = annotation.value();
                    String currentRole = "";
                    if(!Arrays.asList(roles).contains(currentRole)){
                        throw new RuntimeException("没有调用此方法的权限");
                    }
                }
                return method.invoke(targetObject,args);
            }
        }

        public static EmployeeInfoManager getManager(){
            EmployeeInfoManager instance = new EmployeeInfoManager() {
                @Override
                public void updateSalary() {
                    System.out.println("更新薪水！");
                }
            };
            return (EmployeeInfoManager) Proxy.newProxyInstance(
                    instance.getClass().getClassLoader(),
                    new Class<?>[]{EmployeeInfoManager.class},
                    new AccessInvocationHandler<EmployeeInfoManager>(instance));
        }
    }
}
