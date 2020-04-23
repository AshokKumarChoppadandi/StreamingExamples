package com.bigdata.kafka.models;

import java.io.Serializable;

public class Employee implements Serializable {
    private int empId;
    private String eName;
    private int eSalary;
    private String eDept;

    public Employee(int empId, String eName, int eSalary, String eDept) {
        this.empId = empId;
        this.eName = eName;
        this.eSalary = eSalary;
        this.eDept = eDept;
    }

    public Employee() {}

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public int geteSalary() {
        return eSalary;
    }

    public void seteSalary(int eSalary) {
        this.eSalary = eSalary;
    }

    public String geteDept() {
        return eDept;
    }

    public void seteDept(String eDept) {
        this.eDept = eDept;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "empId=" + empId +
                ", eName='" + eName + '\'' +
                ", eSalary=" + eSalary +
                ", eDept='" + eDept + '\'' +
                '}';
    }
}
