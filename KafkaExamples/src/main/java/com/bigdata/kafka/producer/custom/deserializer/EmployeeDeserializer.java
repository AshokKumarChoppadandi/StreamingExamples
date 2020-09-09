package com.bigdata.kafka.producer.custom.deserializer;

import com.bigdata.kafka.producer.utils.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class EmployeeDeserializer implements Deserializer<Employee> {
    private String UTF_ENCODING = "UTF8";
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Nothing
    }

    /*public Employee deserialize(String s, byte[] bytes) {
        if(bytes == null) {
            System.out.println("No data received to Deserialize...!!!");
            return null;
        }

        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        try {
            int employeeId = buffer.getInt();

            int nameLength = buffer.getInt();
            byte[] nameBytesArray = new byte[nameLength];
            buffer.get(nameBytesArray);
            String deserializeName = new String(nameBytesArray, UTF_ENCODING);

            int dobLength = buffer.getInt();
            byte[] dobBytesArray = new byte[dobLength];
            buffer.get(dobBytesArray);
            String dobAsString = new String(dobBytesArray, UTF_ENCODING);
            Date dob = format.parse(dobAsString);

            int salary = buffer.getInt();

            int deptLength = buffer.getInt();
            byte[] deptBytesArray = new byte[deptLength];
            buffer.get(deptBytesArray);
            String dept = new String(deptBytesArray, UTF_ENCODING);

            return new Employee(employeeId, deserializeName, dob, salary, dept);
            //return new Employee(employeeId, deserializeName);

        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
            System.out.println("Unsupported Encoding Exception in Deserialization of Employee Object :: " + uee);
            throw new UnsupportedOperationException(uee);
        } catch (ParseException pe) {
            pe.printStackTrace();
            System.out.println("Parse Exception in Deserialization of Employee Object :: " + pe);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception Occurred in Deserialization of  Employee Object...!!!\n" + e);
            throw new RuntimeException(e);
        }
    }*/
    public Employee deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        Employee emp = null;
        try {
            emp = mapper.readValue(bytes, Employee.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return emp;
    }

    public Employee deserialize(String topic, Headers headers, byte[] data) {
        return null;
    }

    public void close() {
        // Nothing
    }
}
