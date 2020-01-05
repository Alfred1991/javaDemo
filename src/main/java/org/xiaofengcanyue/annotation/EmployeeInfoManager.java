package org.xiaofengcanyue.annotation;

public interface EmployeeInfoManager {
    @Role("manager")
    public void updateSalary();
}
