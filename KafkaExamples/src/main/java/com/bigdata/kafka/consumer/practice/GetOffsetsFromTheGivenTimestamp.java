package com.bigdata.kafka.consumer.practice;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GetOffsetsFromTheGivenTimestamp {
    public static void main(String[] args) {
        Properties properties = new Properties();

        String topicName = "test1";
        Long epochTimestamp = 1720281600000L;

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.113:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "test-group1");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties)) {
            List<PartitionInfo> partitionInfos = consumer.partitionsFor(topicName);
            // int numPartitions = partitionInfos.size();

            Map<TopicPartition, Long> topicPartitionAndTimestampsToSearch = new HashMap<>();

            partitionInfos.forEach(x -> {
                TopicPartition partition = new TopicPartition(x.topic(), x.partition());
                topicPartitionAndTimestampsToSearch.put(partition, epochTimestamp);
            });

            Map<TopicPartition, OffsetAndTimestamp> topicPartitionOffsetAndTimestamp = consumer.offsetsForTimes(topicPartitionAndTimestampsToSearch);

            topicPartitionOffsetAndTimestamp.forEach((x, y) -> {
                System.out.println(x + " -> " + y);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
