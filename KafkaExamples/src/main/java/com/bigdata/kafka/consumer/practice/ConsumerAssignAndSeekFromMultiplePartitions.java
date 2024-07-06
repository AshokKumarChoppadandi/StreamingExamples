package com.bigdata.kafka.consumer.practice;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;

public class ConsumerAssignAndSeekFromMultiplePartitions {
    public static void main(String[] args) {
        Properties properties = new Properties();

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.131:9092");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        long offsetToReadFrom1 = 2L;

        TopicPartition topicPartition1 = new TopicPartition("test1", 0);
        TopicPartition topicPartition2 = new TopicPartition("test1", 1);
        TopicPartition topicPartition3 = new TopicPartition("test1", 2);

        consumer.assign(Arrays.asList(topicPartition1, topicPartition2, topicPartition3));

        consumer.seek(topicPartition1, offsetToReadFrom1);
        List<Long> allTimestamps = new ArrayList<>(getTimestamps(consumer));

        System.out.println("All Timestamps :: " + allTimestamps);

    }

    public static List<Long> getTimestamps(KafkaConsumer consumer) {
        List<Long> timestamps = new ArrayList<>();
        int maxMessages1 = 10;
        boolean readFlag1 = true;
        int numberOfMessagesRead1 = 0;

        while (readFlag1) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> record : records) {
                numberOfMessagesRead1 += 1;
                String message = "Topic :: " + record.topic() + "\n" +
                        "Partition :: " + record.partition() + "\n" +
                        "Offset :: " + record.offset() + "\n" +
                        "Timestamp :: " + record.timestamp() + "\n" +
                        "Key :: " + record.key() + "\n" +
                        "Value :: " + record.value();
                System.out.println(message);
                timestamps.add(record.timestamp());
                if(numberOfMessagesRead1 >= maxMessages1) {
                    readFlag1 = false;
                    break;
                }
            }
        }
        return timestamps;
    }
}
