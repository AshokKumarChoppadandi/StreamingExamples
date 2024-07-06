package com.bigdata.kafka.admin.groups;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.KafkaFuture;

import java.util.Collection;
import java.util.Properties;

public class ConsumerGroupOperations {
    public static void main(String[] args) {
        Properties properties = new Properties();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-ord-cpe-prod.cnvr.in:9092");
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "java-client-1");

        try (AdminClient client = AdminClient.create(properties)) {
            ListTopicsOptions topicsOptions = new ListTopicsOptions();
            topicsOptions.listInternal(false);

            ListTopicsResult topicsResult = client.listTopics();
            KafkaFuture<Collection<TopicListing>> topicListings = topicsResult.listings();
            Collection<TopicListing> topicsList = topicListings.get();

            topicsList.forEach(
                    topic -> {
                        System.out.println(topic.name());
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
