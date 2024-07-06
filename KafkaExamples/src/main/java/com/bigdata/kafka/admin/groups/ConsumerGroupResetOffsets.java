package com.bigdata.kafka.admin.groups;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ConsumerGroupResetOffsets {
    public static void main(String[] args) {
        Properties properties = new Properties();

        String topicName = "test1";

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.113:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "test-group1");

        long offset1ToRead = 25L;
        long offset2ToRead = 0L;
        long offset3ToRead = 15L;

        TopicPartition partition1 = new TopicPartition(topicName, 0);
        TopicPartition partition2 = new TopicPartition(topicName, 1);
        TopicPartition partition3 = new TopicPartition(topicName, 2);

        List<TopicPartition> topicPartitions = Arrays.asList(partition1, partition2, partition3);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties)) {
            consumer.assign(topicPartitions);
            consumer.seek(partition1, offset1ToRead);
            consumer.seek(partition2, offset2ToRead);
            consumer.seek(partition3, offset3ToRead);

            consumer.commitSync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
