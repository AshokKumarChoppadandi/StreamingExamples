package com.bigdata.kafka.admin;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.KafkaFuture;

import java.util.Collection;
import java.util.Properties;

public class ListKafkaTopics {
    public static void main(String[] args) {
        Properties properties = new Properties();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.113:9092");
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
