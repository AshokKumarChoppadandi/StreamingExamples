package com.bigdata.kafka.deserializer;

import com.bigdata.kafka.models.Employee;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class EmployeeDeserializer implements Deserializer<Employee> {
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    public Employee deserialize(String s, byte[] bytes) {
        return SerializationUtils.deserialize(bytes);
    }

    public void close() {

    }
}
