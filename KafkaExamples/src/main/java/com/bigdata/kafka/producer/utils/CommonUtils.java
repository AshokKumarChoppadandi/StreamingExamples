package com.bigdata.kafka.producer.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CommonUtils {
    private Properties properties;
    public CommonUtils() {
        this.properties = new Properties();
    }
    public Properties getProperties(String configPath) {
        try {
            InputStream inputStream = new FileInputStream(configPath);
            properties.load(inputStream);
            return properties;
        } catch (FileNotFoundException e1) {
            throw new RuntimeException(e1);
        } catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }
    public void showProperties(Properties properties1) {
        System.out.println("bootstrap.servers" + properties1.getProperty("bootstrap.servers"));
        System.out.println("acks" + properties1.getProperty("acks"));
        System.out.println("retries" + properties1.getProperty("retries"));
        System.out.println("batch.size" + properties1.getProperty("batch.size"));
        System.out.println("linger.ms" + properties1.getProperty("linger.ms"));
        System.out.println("buffer.memory" + properties1.getProperty("buffer.memory"));
        System.out.println("key.serializer" + properties1.getProperty("key.serializer"));
        System.out.println("value.serializer" + properties1.getProperty("value.serializer"));
    }
}
