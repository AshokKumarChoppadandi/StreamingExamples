package com.bigdata.kafka.consumer;

import com.bigdata.kafka.deserializer.EmployeeDeserializer;
import com.bigdata.kafka.deserializer.UsedCarDeserializer;
import com.bigdata.kafka.models.Employee;
import com.bigdata.kafka.models.UsedCar;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class UsedCarConsumer {
    public static void main(String[] args) {
        Properties properties = new Properties();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UsedCarDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "used-car-consumer-group-1");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);

        Consumer<String, UsedCar> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList("used-cars"));

        while (true) {
            ConsumerRecords<String, UsedCar> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, UsedCar> record : records) {
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
