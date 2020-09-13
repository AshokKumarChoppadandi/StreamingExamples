package com.bigdata.kafka.producer.utils;

import java.util.Date;

public class Employee {
    private int employeeId;
    private String employeeName;
    private Date dateOfBirth;
    private int salary;
    private String dept;

    public Employee(int employeeId, String employeeName, Date dateOfBirth, int salary, String dept) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.dateOfBirth = dateOfBirth;
        this.salary = salary;
        this.dept = dept;
    }

    public Employee(int employeeId, String employeeName) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public int getSalary() {
        return salary;
    }

    public String getDept() {
        return dept;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", salary=" + salary +
                ", dept='" + dept + '\'' +
                '}';
    }
}
