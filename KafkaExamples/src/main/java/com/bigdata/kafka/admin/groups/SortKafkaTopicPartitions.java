package com.bigdata.kafka.admin.groups;

import org.apache.kafka.common.TopicPartition;

import java.util.*;
import java.util.stream.Collectors;

public class SortKafkaTopicPartitions {
    public static void main(String[] args) {
        System.out.println(generateRandomTopicPartitionsAndOffsets("Test", 10));

        Comparator<TopicPartition> byTopicPartition = Comparator.comparing(TopicPartition::toString);
        Map<TopicPartition, Integer> sortedMap = generateRandomTopicPartitionsAndOffsets("Test", 10)
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(byTopicPartition))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        Set<TopicPartition> topicPartitions = sortedMap.keySet();

        for (TopicPartition topicPartition: topicPartitions) {
            System.out.println(topicPartition + " -> " + sortedMap.get(topicPartition));
        }


    }

    private static Map<TopicPartition, Integer> generateRandomTopicPartitionsAndOffsets(String topic, int numberOfPartitions) {
        Map<TopicPartition, Integer> topicPartitionsWithOffsets = new HashMap<>();
        Random random = new Random();

        for (int i = numberOfPartitions - 1; i >= 0 ; i--) {
            TopicPartition partition = new TopicPartition(topic, i);
            int offset = random.nextInt(1000000);
            topicPartitionsWithOffsets.put(partition, offset);
        }
        return topicPartitionsWithOffsets;
    }
}
