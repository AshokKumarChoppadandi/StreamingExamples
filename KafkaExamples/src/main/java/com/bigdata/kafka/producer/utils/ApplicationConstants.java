package com.bigdata.kafka.producer.utils;

public class ApplicationConstants {
    public static String CONFIG_PATH = "./src/main/resources/kafka/config.properties";
    public static String BOOTSTRAP_SERVERS = "bootstrap.servers";
    public static String ACKS = "acks";
    public static String RETRIES = "retries";
    public static String BATCH_SIZE = "batch.size";
    public static String LINGER_MS = "linger.ms";
    public static String BUFFER_MEMORY = "buffer.memory";
    public static String STRING_KEY_SERIALIZER = "key.serializer";
    public static String STRING_VALUE_SERIALIZER = "value.serializer";

    public static String CARS_INPUT_DIRECTORY = "./src/main/resources/cars/";
    public static String TEST_FILE = "./src/main/resources/input/TestFile.txt";

    public static String EMPLOYEE_TOPIC = "employee";
}
