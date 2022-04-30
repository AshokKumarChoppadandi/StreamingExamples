package com.bigdata.kafka.producer.edgar_logs;

import com.bigdata.kafka.producer.utils.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.Date;
import java.util.Properties;

public class ConvertObjectToJson {
    public static void main(String[] args) {
        Employee employee = new Employee(123, "Ashok Kumar Choppadandi", new Date(), 10000, "IT");

        ObjectMapper mapper = new ObjectMapper();

        try {
            String employeeAsJsonString = mapper.writeValueAsString(employee);
            System.out.println(employeeAsJsonString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String message = "101.81.133.jja,2017-06-30,00:00:00,0.0,1608552.0,0001047469-17-004337,-index.htm,200.0,80251.0,1.0,0.0,0.0,9.0,0.0,";
        String[] fields = message.split(",");
        System.out.println("Array Length :: " + fields.length + ", Last Element :: " + fields[fields.length - 1]);

        Properties properties = new Properties();
        PrintWriter writer = new PrintWriter(System.out);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("/home/ashok/IdeaProjects/StreamingExamples/KafkaExamples/src/main/resources/kafka/edgar-avro-config.properties");
            properties.load(inputStream);
            properties.list(writer);
            writer.flush();
        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

    }
}
