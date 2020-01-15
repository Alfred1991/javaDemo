package org.xiaofengcanyue.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解的含义可以理解为java源代码中的元数据。
 *
 * 三个元注解： @Target @Retention @Inherited（只对ElementType.TYPE有效）
 * java标准库中的一般注解： @Override @Deprecated @SuppressWarnings
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Author {

    //没有设置默认值，需要在使用时显示地指定
    String name();
    String email();
    boolean enableEmailNotification() default true;

}
