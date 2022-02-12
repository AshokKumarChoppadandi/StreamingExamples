package com.bigdata.kafka.producer.custom;

import com.bigdata.kafka.producer.utils.CommonUtils;
import com.bigdata.kafka.producer.utils.Employee;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;
import static com.bigdata.kafka.producer.utils.ApplicationConstants.*;

public class EmployeeConsumer {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.setProperty(BOOTSTRAP_SERVERS, "localhost:9092");
        properties.setProperty(GROUP_ID, EMPLOYEE_CONSUMER_GROUP);
        properties.setProperty(KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty(VALUE_DESERIALIZER, EMPLOYEE_VALUE_DESERIALIZER);
        properties.setProperty(AUTO_OFFSET_RESET_CONFIG, EARLIEST);

        CommonUtils utils = new CommonUtils();
        utils.showConsumerProperties(properties);
        KafkaConsumer<String, Employee> consumer = new KafkaConsumer<String, Employee>(properties);
        consumer.subscribe(Arrays.asList("test-1"));

        while (true) {
            ConsumerRecords<String, Employee> records = consumer.poll(5);
            for (ConsumerRecord<String, Employee> record : records) {
                System.out.println("KEY :: " + record.key() + ", EMPLOYEE :: " + record.value().toString());
            }
        }
    }
}
