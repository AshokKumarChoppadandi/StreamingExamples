package com.bigdata.avro;

import org.apache.avro.reflect.AvroDefault;
import org.apache.avro.reflect.Nullable;

public class ReflectedCustomer {
    private String firstName;
    @Nullable private String lastName;
    private int age;
    private float height;
    private float weight;
    @AvroDefault("false") private boolean automatedEmail;

    public ReflectedCustomer() {}

    public ReflectedCustomer(String firstName, String lastName, int age, float height, float weight, boolean automatedEmail) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.automatedEmail = automatedEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public boolean isAutomatedEmail() {
        return automatedEmail;
    }

    public void setAutomatedEmail(boolean automatedEmail) {
        this.automatedEmail = automatedEmail;
    }

    @Override
    public String toString() {
        return "ReflectedCustomer{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", height=" + height +
                ", weight=" + weight +
                ", automatedEmail=" + automatedEmail +
                '}';
    }
}
