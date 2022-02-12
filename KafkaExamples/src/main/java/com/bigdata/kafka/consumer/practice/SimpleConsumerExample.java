package com.bigdata.kafka.consumer.practice;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class SimpleConsumerExample {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(SimpleConsumerExample.class.getName());

        String bootstrapServers = "127.0.0.1:9092";
        String topic = "second-topic";
        String consumerGroup = "group1";

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        consumer.subscribe(Collections.singleton(topic));

        while(true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

            for (ConsumerRecord<String, String> record : records) {
                String message = "Topic :: " + record.topic() + "\n" +
                        "Partition :: " + record.partition() + "\n" +
                        "Offset :: " + record.offset() + "\n" +
                        "Timestamp :: " + record.timestamp() + "\n" +
                        "Key :: " + record.key() + "\n" +
                        "Value :: " + record.value();
                logger.info(message);
                System.out.println(message);
            }
        }
    }
}
