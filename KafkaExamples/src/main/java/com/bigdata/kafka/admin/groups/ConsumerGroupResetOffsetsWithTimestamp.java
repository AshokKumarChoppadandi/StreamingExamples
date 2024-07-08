package com.bigdata.kafka.admin.groups;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConsumerGroupResetOffsetsWithTimestamp {
    public static void main(String[] args) {
        CommandLine commandLine = getCommandLine(args);
        String propertiesFile = commandLine.getOptionValue("propertiesFile");
        String bootstrapServerURL = commandLine.getOptionValue("bootstrapServer");
        String connectServerURL = commandLine.getOptionValue("connectServer");
        Long epochTimestamp = Long.parseLong(commandLine.getOptionValue("epochTimestamp"));
        String action = commandLine.getOptionValue("action");

        JsonNode kafkaConnectorConfig = getKafkaConnectorConfigs(propertiesFile);
        String kafkaConnectorName = kafkaConnectorConfig.get("name").asText();
        String kafkaTopicName = kafkaConnectorConfig.get("config").get("topics").asText();
        String consumerGroup = "test-" + kafkaConnectorName;

        Properties properties = getProperties(bootstrapServerURL, consumerGroup);
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        Map<TopicPartition, OffsetAndTimestamp> topicPartitionOffsetAndTimestamp = getOffsetsFromTheGiveTimestamp(consumer, kafkaTopicName, epochTimestamp);
        Set<TopicPartition> topicPartitions = topicPartitionOffsetAndTimestamp.keySet();

        consumer.assign(topicPartitions);

        topicPartitions.forEach(x -> {
            consumer.seek(x, topicPartitionOffsetAndTimestamp.get(x).offset());
        });

        // 1720425600000
        consumer.commitSync();
        consumer.close();
        System.out.println("Completed");
    }

    private static CommandLine getCommandLine(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = getOptions();
        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static Options getOptions() {
        Options options = new Options();

        options.addRequiredOption("p", "propertiesFile", true, "Properties File Path");
        options.addRequiredOption("b", "bootstrapServer", true, "Kafka Bootstrap Server URL");
        options.addRequiredOption("c", "connectServer", true, "Kafka Connect Server URL");
        options.addOption("t", "epochTimestamp", true, "Epoch timestamp to reset offsets in Milliseconds");
        options.addOption("a", "action", true, "Action to perform - allowed values are dryRun | execute | status | stop");
        options.addOption("h", "help", false, "Help / Usage");

        return options;
    }

    private static void printHelp(String[] args) {
        Options options = getOptions();
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -cp JavaExamples-1.0-SNAPSHOT.jar NameArguments [options]", options);
    }

    private static JsonNode getKafkaConnectorConfigs(String propertiesFile) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(new File(propertiesFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<TopicPartition, OffsetAndTimestamp> getOffsetsFromTheGiveTimestamp(KafkaConsumer<String, String> consumer, String topicName, Long epochTimestamp) {
        List<PartitionInfo> partitionInfos = consumer.partitionsFor(topicName);
        Map<TopicPartition, Long> topicPartitionAndTimestampsToSearch = new HashMap<>();
        partitionInfos.forEach(x -> {
            TopicPartition partition = new TopicPartition(x.topic(), x.partition());
            topicPartitionAndTimestampsToSearch.put(partition, epochTimestamp);
        });
        return consumer.offsetsForTimes(topicPartitionAndTimestampsToSearch);
    }

    private static Properties getProperties (String bootstrapServerURL, String consumerGroupName) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerURL);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupName);

        return properties;
    }
}
