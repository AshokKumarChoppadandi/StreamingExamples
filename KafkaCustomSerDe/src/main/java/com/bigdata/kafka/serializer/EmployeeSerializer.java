package com.bigdata.kafka.serializer;

import com.bigdata.kafka.models.Employee;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.commons.lang3.SerializationUtils;

import java.util.Map;

public class EmployeeSerializer implements Serializer<Employee> {

    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    public byte[] serialize(String s, Employee employee) {
        return SerializationUtils.serialize(employee);
    }

    public void close() {

    }
}
