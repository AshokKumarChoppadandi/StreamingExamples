package com.bigdata.kafka;

import com.bigdata.kafka.deserializer.EmployeeDeserializer;
import com.bigdata.kafka.models.Employee;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092  --group employee-consumer-group-1 --topic employee --reset-offsets --to-earliest --execute
 */

public class EmployeeConsumer {
    public static void main(String[] args) {
        Properties properties = new Properties();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EmployeeDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "employee-consumer-group-1");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);

        Consumer<String, Employee> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList("employee"));
        while (true) {
            ConsumerRecords<String, Employee> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, Employee> record : records) {
                System.out.println(
                        "Topic :: " + record.topic() + "," +
                                "Partition :: " + record.partition() + "," +
                                "Offset :: " + record.offset() + "," +
                                "Timestamp :: " + record.timestamp() + "," +
                                record.value()
                );
            }
        }
    }
}
