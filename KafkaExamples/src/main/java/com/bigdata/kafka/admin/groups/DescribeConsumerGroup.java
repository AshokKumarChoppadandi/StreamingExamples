package com.bigdata.kafka.admin.groups;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class DescribeConsumerGroup {
    public static void main(String[] args) {
        Properties properties = new Properties();

        String consumerGroup = "test-group1";
        Collection<String> groupIds = Collections.singletonList(consumerGroup);

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.0.113:9092");
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "java-admin-client");

        try (AdminClient client = AdminClient.create(properties)) {
            DescribeConsumerGroupsResult result = client.describeConsumerGroups(groupIds);
            Map<String, KafkaFuture<ConsumerGroupDescription>> describedGroups = result.describedGroups();

            ConsumerGroupDescription consumerGroupDescription = describedGroups.get(consumerGroup).get();
            Collection<MemberDescription> members = consumerGroupDescription.members();
            String assignor = consumerGroupDescription.partitionAssignor();

            if (members.isEmpty()) {
                System.out.println("No active members exists in Consumer Group - " + consumerGroup);
            } else {
                System.out.println("Consumer Group '" + consumerGroup + "' Details:\n**************************************");
                members.forEach(x -> {
                    String consumerId = x.consumerId();
                    MemberAssignment assignment = x.assignment();
                    String clientId = x.clientId();
                    Set<TopicPartition> topicPartitions = assignment.topicPartitions();

                    System.out.println("Consumer ID - " + consumerId + "\n" +
                            "Client ID - " + clientId + "\n" +
                            "Assignor - " + assignor);
                    System.out.println("\nTopic Partitions:\n**********************");
                    topicPartitions.forEach(System.out::println);
                });
            }


            System.out.println();

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}
