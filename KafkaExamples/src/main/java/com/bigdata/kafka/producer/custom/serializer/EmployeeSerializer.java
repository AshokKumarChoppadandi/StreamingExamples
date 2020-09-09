package com.bigdata.kafka.producer.custom.serializer;

import com.bigdata.kafka.producer.utils.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.ByteBuffer;
import java.util.Map;

public class EmployeeSerializer implements Serializer<Employee> {
    private String UTF_ENCODING = "UTF8";
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Nothing
    }

    /*public byte[] serialize(String s, Employee employee) {
        int nameLength;
        byte[] name;

        int dobLength;
        byte[] dob;

        int deptLength;
        byte[] dept;

        try {
            if (employee == null)
                return null;

            int employeeId = employee.getEmployeeId();
            String employeeName = employee.getEmployeeName();
            String employeeDOB = employee.getDateOfBirth().toString();
            int employeeSalary = employee.getSalary();
            String employeeDept = employee.getDept();

            nameLength = employeeName.length();
            name = employeeName.getBytes(UTF_ENCODING);

            dobLength = employeeDOB.length();
            dob = employeeDOB.getBytes(UTF_ENCODING);

            deptLength = employeeDept.length();
            dept = employeeDept.getBytes(UTF_ENCODING);

            ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + nameLength + 4 + dobLength + 4 + 4 + deptLength);
            //ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + nameLength);
            buffer.putInt(employeeId);

            buffer.putInt(nameLength);
            buffer.put(name);

            buffer.putInt(dobLength);
            buffer.put(dob);

            buffer.putInt(employeeSalary);

            buffer.putInt(deptLength);
            buffer.put(dept);

            return buffer.array();

        } catch (Exception e) {
            throw new SerializationException("Unbale to Serialize Employee Object...!!!");
        }
    }*/

    public byte[] serialize(String s, Employee employee) {
        byte[] returnArr = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            returnArr = mapper.writeValueAsString(employee).getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnArr;
    }

    public byte[] serialize(String topic, Headers headers, Employee data) {
        return new byte[0];
    }

    public void close() {
        // Nothing
    }
}
