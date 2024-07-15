package com.bigdata.kafka.admin.groups;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static java.util.Map.Entry;

public class ConsumerGroupResetOffsetsWithTimestamp {
    public static void main(String[] args) {
        CommandLine commandLine = getCommandLine(args);
        String propertiesFile = commandLine.getOptionValue("propertiesFile");
        String bootstrapServerURL = commandLine.getOptionValue("bootstrapServer");
        String connectServerURL = commandLine.getOptionValue("connectServer");
        // TODO: Add check to validate the timestamp
        Long epochTimestamp = Long.parseLong(commandLine.getOptionValue("epochTimestamp"));
        // TODO: If not action provided - Help should be printed
        String action = commandLine.getOptionValue("action");

        JsonNode kafkaConnectorConfig = getKafkaConnectorConfigs(propertiesFile);
        String kafkaConnectorName = kafkaConnectorConfig.get("name").asText();
        String kafkaTopicName = kafkaConnectorConfig.get("config").get("topics").asText();
        String consumerGroup = "connect-" + kafkaConnectorName;

        String kafkaConnectVersion = getKafkaConnectVersion(connectServerURL);
        System.out.println("Kafka Connect Version : " + kafkaConnectVersion);

        switch (action) {
            case "dryRun":
                // resetKafkaConsumerOffsetsDryRun
            case "execute":
                // resetKafkaConsumerOffsets
                Properties properties = getProperties(bootstrapServerURL, consumerGroup);
                KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
                Map<TopicPartition, OffsetAndTimestamp> topicPartitionOffsetAndTimestamp = getOffsetsFromTheGiveTimestamp(consumer, kafkaTopicName, epochTimestamp);
                if (action.equals("execute")) {
                    System.out.println("Stopping Connector, if it is running.");
                    stopConnector(connectServerURL, kafkaConnectorName);
                    System.out.println("Resetting offsets...");
                    resetKafkaConsumerOffsets(consumer, topicPartitionOffsetAndTimestamp);
                } else {
                    System.out.println("Current Offsets for Kafka Connector - ");
                    statusOfConnector(bootstrapServerURL, connectServerURL, kafkaConnectorName, kafkaTopicName);
                    System.out.println("\nNew Offsets for Kafka Connector when reset to Epoch Timestamp - " + epochTimestamp);
                    printTopicPartitionOffsetAndTimestamp(topicPartitionOffsetAndTimestamp);
                }
                consumer.close();
                break;
            case "start":
                // startConnector
                startConnector(connectServerURL, propertiesFile);
                break;
            case "status":
                // statusOfConnector
                statusOfConnector(bootstrapServerURL, connectServerURL, kafkaConnectorName, kafkaTopicName);
                break;
            case "describe":
                // describeConnector
                describeConnector(connectServerURL, kafkaConnectorName);
                break;
            case "stop":
                // stopConnector
                if (kafkaConnectVersion.startsWith("7.")) {
                    stopConnector(connectServerURL, kafkaConnectorName);
                } else {
                    System.out.println("Kafka Connect Server version is - " + kafkaConnectVersion + ", STOP is not supported. Deleting the connector");
                    deleteConnector(connectServerURL, kafkaConnectorName);
                }
                break;
            case "delete":
                // deleteConnector
                deleteConnector(connectServerURL, kafkaConnectorName);
                break;
            case "resume":
                if (kafkaConnectVersion.startsWith("7.")) {
                    resumeConnector(connectServerURL, kafkaConnectorName);
                } else {
                    System.out.println("Kafka Connect Server version is - " + kafkaConnectVersion + ", RESUME is not supported. Starting the connector");
                    startConnector(connectServerURL, propertiesFile);
                }
                break;
            default:
                printHelp();
                // Throw error saying only the defined actions are allowed
                break;
        }
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
        options.addOption("a", "action", true, "Action to perform - allowed values are dryRun | execute | start | status | stop | delete | resume");
        options.addOption("h", "help", false, "Help / Usage");

        return options;
    }

    private static void printHelp() {
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

    private static Properties getProperties(String bootstrapServerURL, String consumerGroupName) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerURL);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupName);

        return properties;
    }

    private static void resetKafkaConsumerOffsets(KafkaConsumer<String, String> consumer, Map<TopicPartition, OffsetAndTimestamp> topicPartitionOffsetAndTimestamp) {
        Set<TopicPartition> topicPartitions = topicPartitionOffsetAndTimestamp.keySet();
        consumer.assign(topicPartitions);

        topicPartitions.forEach(x -> {
            consumer.seek(x, topicPartitionOffsetAndTimestamp.get(x).offset());
        });
        consumer.commitSync();
        consumer.close();
        System.out.println("Re-setting Offsets Completed");
    }

    private static void startConnector(String connectServerURL, String connectorConfigFilePath) {
        // Take the connector file and start the connector with POST REST request and return the status of the connector
        // curl -X POST -H "Content-Type: application/json" -H "Accept: application/json" -d @edgar-logs-pg-sink.json http://192.168.0.231:8083/connectors

        try {
            URL url = new URL("http://" + connectServerURL + "/connectors");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // System.out.println("Calling getKafkaConnectorConfigs - " + connectorConfigFilePath);
            JsonNode jsonNode = getKafkaConnectorConfigs(connectorConfigFilePath);
            // System.out.println("Json String - \n" + jsonNode.toString());
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(jsonNode.toString());
            writer.flush();

            String response = getHTTPResponse(connection);
            if (response != null) {
                System.out.println("Kafka Connector started successfully");
                System.out.println("Response ::\n" + response);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void describeConnector(String connectServerURL, String connectorName) {
        try {
            URL url = new URL("http://" + connectServerURL + "/connectors/" + connectorName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            String response = getHTTPResponse(connection);
            System.out.println("Describe Response - " + response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void statusOfConnector(String bootstrapServerURL, String connectServerURL, String connectorName, String kafkaTopic) {
        // Take connector name as input, Get the status of the connector with GET REST request with tasks info and display the current offsets

        String consumerGroup = "connect-" + connectorName;

        try {
            URL obj = new URL("http://" + connectServerURL + "/connectors/" + connectorName + "/status");
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");

            String jsonResponse = getHTTPResponse(connection);
            JsonNode jsonNode = convertStringToJson(jsonResponse);

            String connectorStatus = jsonNode.get("connector").get("state").asText();
            System.out.println("Status of Kafka connector - '" + connectorName + "'" + " is - '" + connectorStatus.toUpperCase() + "'");

            System.out.println("\n\nBelow are the connector details:\n-----------------------------------\nWORKER\t\t\t|\tNUMBER OF TASKS\t|\t TYPE");

            // Connector details
            // Total Number of Tasks
            // Type - sink / source

            // Worker ID - XYZ
            // Total Number of Tasks - Count(tasks)

            String connectorWorkerID = jsonNode.get("connector").get("worker_id").asText();
            JsonNode tasks = jsonNode.get("tasks");
            String totalTasks = String.valueOf(tasks.size());
            String connectorType = jsonNode.get("type").asText();

            // Tasks Details
            // Task 1 - status
            // Task 2 - status
            // etc.,
            System.out.println(connectorWorkerID + "\t|\t" + totalTasks + "\t\t|\t" + connectorType);

            System.out.println("\n\t\tTasks Details:\t\t\n---------------------------------");
            System.out.println("ID\t|\tSTATE\t|\tWORKER_ID");

            for (JsonNode task : tasks) {
                System.out.println(task.get("id").asInt() + "\t|\t" + task.get("state").asText() + "\t|\t" + task.get("worker_id").asText());
            }

            Map<TopicPartition, Long> partitionsOffsets = listConsumerGroupOffsets(bootstrapServerURL, consumerGroup);
            // Map<Integer, Long> partitionsOffsetsSorted = partitionsOffsets.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            // List<TopicPartition> topicPartitions = partitionsOffsets.keySet().stream().sorted((t1, t2) -> (t1.partition() > t2.partition() ? t1 : t2).partition()).collect(Collectors.toList());

            Map<TopicPartition, Long> sortedPartitionsOffsets = sortTopicPartitionsWithOffsets(partitionsOffsets);

            System.out.println("\n\nCurrent Offsets of Kafka Connector\n------------------------------------------");
            System.out.println("TOPIC PARTITION\t\t|\t\tPARTITION\t\t|\t\tOFFSETS");

            sortedPartitionsOffsets.forEach((x, y) -> {
                System.out.println(x.topic() + "\t\t|\t\t" + x.partition() + "\t\t|\t\t" + y);
            });

            /*for (TopicPartition topicPartition: topicPartitions) {
                System.out.println(topicPartition + "\t\t|\t\t" + partitionsOffsets.get(topicPartition));
            }*/

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // GET - http://192.168.0.231:8081/connectors

        // Bootstrap Server URL, Connect Server URL, Connector Name, Consumer Group

        // 1. Get the status of the connector with below details:
        // 1. Number of Tasks
        // 2. Status of the Tasks
        // 3. Connector configuration
        // 4. Current Offsets from the Consumer Group
    }

    private static void stopConnector(String connectServerURL, String connectorName) {
        // Take connector name as input, Get the status of the connector with GET REST request & stop the connector only if it is Running
        try {
            URL url = new URL("http://" + connectServerURL + "/connectors/" + connectorName + "/stop");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            String response = getHTTPResponse(connection);
            System.out.println("Stop Connector Response - " + response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void deleteConnector(String connectServerURL, String connectorName) {
        // Take connector name as input, Delete the connector with GET REST request
        try {
            URL url = new URL("http://" + connectServerURL + "/connectors/" + connectorName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            String response = getHTTPResponse(connection);
            System.out.println("Delete Response - " + response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void resumeConnector(String connectServerURL, String connectorName) {
        // Take connector name as input, Get the status of the connector with GET REST request & resume the connector only if it is stopped or not found
        try {
            URL url = new URL("http://" + connectServerURL + "/connectors/" + connectorName + "/resume");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            String response = getHTTPResponse(connection);
            System.out.println("Resume Response - " + response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printTopicPartitionOffsetAndTimestamp(Map<TopicPartition, OffsetAndTimestamp> topicPartitionOffsetAndTimestamp) {
        System.out.println("\nTopic\t\t|\t\tPartition\t\t|\t\tOffset");
        topicPartitionOffsetAndTimestamp.keySet().forEach(x -> {
            System.out.println(x.topic() + "\t\t|\t\t" + x.partition() + "\t\t|\t\t" + topicPartitionOffsetAndTimestamp.get(x).offset());
        });
    }

    private static String getKafkaConnectVersion(String connectServerURL) {
        try {
            URL url = new URL("http://" + connectServerURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            String connectServerVersion = getHTTPResponse(connection);
            JsonNode jsonNode = convertStringToJson(connectServerVersion);

            return jsonNode.get("version").asText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getHTTPResponse(HttpURLConnection connection) {
        int responseCode;
        try {
            responseCode = connection.getResponseCode();
            // String responseMessage = connection.getResponseMessage();
            // System.out.println("Response Message :: " + responseMessage);

            String responseString;
            switch (responseCode) {
                case HttpURLConnection.HTTP_OK:
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    responseString = response.toString();
                    break;
                case HttpURLConnection.HTTP_NO_CONTENT:
                    responseString = "NO CONTENT";
                    break;
                case HttpURLConnection.HTTP_ACCEPTED:
                    responseString = "ACCEPTED";
                    break;
                default:
                    responseString = null;
                    break;
            }

            return responseString;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static JsonNode convertStringToJson(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<TopicPartition, Long> listConsumerGroupOffsets(String bootstrapServerURL, String consumerGroup) {
        Properties properties = new Properties();

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerURL);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "java-admin-client");
        System.out.println("Consumer Group - " + consumerGroup);

        AdminClient client = AdminClient.create(properties);
        ListConsumerGroupOffsetsResult result = client.listConsumerGroupOffsets(consumerGroup);
        try {
            Map<TopicPartition, OffsetAndMetadata> partitionsOffsetsMetadata = result.partitionsToOffsetAndMetadata().get();
            Map<TopicPartition, Long> partitionsOffsets = new HashMap<>();
            partitionsOffsetsMetadata.forEach((x, y) -> {
                partitionsOffsets.put(x, y.offset());
            });

            return partitionsOffsets;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            client.close();
        }
    }

    private static Map<TopicPartition, Long> sortTopicPartitionsWithOffsets(Map<TopicPartition, Long> topicPartitionsWithOffsets) {
        Comparator<TopicPartition> byTopicPartition = Comparator.comparing(TopicPartition::toString);
        return topicPartitionsWithOffsets
                .entrySet()
                .stream()
                .sorted(Entry.comparingByKey(byTopicPartition))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private static String boldString(String str) {
        return "\033[0;1m" + str;
    }
}
