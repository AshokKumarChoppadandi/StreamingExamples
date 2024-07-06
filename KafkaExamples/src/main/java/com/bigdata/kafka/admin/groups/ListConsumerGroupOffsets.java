package com.bigdata.kafka.admin.groups;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Map;
import java.util.Properties;

public class ListConsumerGroupOffsets {
    public static void main(String[] args) {
        Properties properties = new Properties();

        String consumerGroup = "test-group1";
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.113:9092");
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "java-admin-client");

        try (AdminClient client = AdminClient.create(properties)) {
            ListConsumerGroupOffsetsResult result = client.listConsumerGroupOffsets(consumerGroup);
            Map<TopicPartition, OffsetAndMetadata> partitionsToOffsets = result.partitionsToOffsetAndMetadata().get();

            partitionsToOffsets.forEach((x, y) -> {
                System.out.println(x + " -> " + y);

            });
        } catch (Exception e) {

        }
    }
}
