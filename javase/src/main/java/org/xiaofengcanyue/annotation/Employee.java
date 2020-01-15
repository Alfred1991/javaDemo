package org.xiaofengcanyue.annotation;

/**
 * 在注解的声明中，除了表示配置元素的方法外，还可以包含常量声明、类和接口声明、枚举类型声明以及注释类型声明。
 */
public @interface Employee {
    int s = 1;
    enum EMPLOYEE_TYPE {REGULAR,CONTRACT};
    EMPLOYEE_TYPE value();
}
