package com.bigdata.kafka.consumer.practice;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class ConsumerAssignAndSeekExample {
    public static void main(String[] args) {
        Properties properties = new Properties();

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        long offsetToReadFrom = 10L;
        TopicPartition topicPartition1 = new TopicPartition("second-topic", 0);
        // TopicPartition topicPartition2 = new TopicPartition("second-topic", 1);
        // consumer.assign(Arrays.asList(topicPartition1, topicPartition2));
        consumer.assign(Collections.singletonList(topicPartition1));

        consumer.seek(topicPartition1, offsetToReadFrom);

        int maxMessages = 10;
        boolean readFlag = true;
        int numberOfMessagesRead = 0;

        while (readFlag) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> record : records) {
                numberOfMessagesRead += 1;
                String message = "Topic :: " + record.topic() + "\n" +
                        "Partition :: " + record.partition() + "\n" +
                        "Offset :: " + record.offset() + "\n" +
                        "Timestamp :: " + record.timestamp() + "\n" +
                        "Key :: " + record.key() + "\n" +
                        "Value :: " + record.value();
                System.out.println(message);

                if(numberOfMessagesRead >= maxMessages) {
                    readFlag = false;
                    break;
                }
            }
        }

        consumer.close();
    }
}
