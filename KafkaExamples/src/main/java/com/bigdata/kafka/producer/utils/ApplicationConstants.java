package com.bigdata.kafka.producer.utils;

public class ApplicationConstants {
    public static String CONFIG_PATH = "./src/main/resources/kafka/config.properties";
    public static String BOOTSTRAP_SERVERS = "bootstrap.servers";
    public static String ACKS = "acks";
    public static String RETRIES = "retries";
    public static String BATCH_SIZE = "batch.size";
    public static String LINGER_MS = "linger.ms";
    public static String BUFFER_MEMORY = "buffer.memory";
    public static String KEY_SERIALIZER = "key.serializer";
    public static String VALUE_SERIALIZER = "value.serializer";

    public static String GROUP_ID = "group.id";
    public static String KEY_DESERIALIZER = "key.deserializer";
    public static String VALUE_DESERIALIZER = "value.deserializer";
    public static String AUTO_OFFSET_RESET_CONFIG = "auto.offset.reset";
    public static String EARLIEST = "earliest";

    public static String CARS_INPUT_DIRECTORY = "./src/main/resources/cars/";
    public static String TEST_FILE = "./src/main/resources/input/TestFile.txt";

    public static String EMPLOYEE_TOPIC = "employee-new";
    public static String EMPLOYEE_VALUE_SERIALIZER = "com.bigdata.kafka.producer.custom.serializer.EmployeeSerializer";
    public static String EMPLOYEE_VALUE_DESERIALIZER = "com.bigdata.kafka.producer.custom.deserializer.EmployeeDeserializer";
    public static String EMPLOYEE_CONSUMER_GROUP = "employee-group1";

    public static String TOPIC_NAME = "topic.name";
    public static String OUTPUT_FORMAT = "output.format";
    public static String DEFAULT_OUTPUT_FORMAT = "csv";
    public static String AVRO_OUTPUT_FORMAT = "avro";
    public static String SCHEMA_FILE = "schema.file";
    public static String INPUT_LOGS_DIR = "input.logs.dir";
    public static long POLLING_INTERVAL = 10 * 1000;
}
