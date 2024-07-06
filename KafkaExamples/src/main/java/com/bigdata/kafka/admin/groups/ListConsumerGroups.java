package com.bigdata.kafka.admin.groups;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.ListConsumerGroupsResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Collection;
import java.util.Properties;

public class ListConsumerGroups {
    public static void main(String[] args) {
        Properties properties = new Properties();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.113:9092");
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "java-admin-client");

        try (AdminClient client = AdminClient.create(properties)) {
            ListConsumerGroupsResult groupsList = client.listConsumerGroups();

            Collection<ConsumerGroupListing> groups = groupsList.valid().get();

            groups.forEach(x -> {
                System.out.println(x.groupId());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
