package com.bigdata.kafka.producer.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import static com.bigdata.kafka.producer.utils.ApplicationConstants.*;

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
        System.out.println(BOOTSTRAP_SERVERS + properties1.getProperty(BOOTSTRAP_SERVERS));
        System.out.println(ACKS + properties1.getProperty(ACKS));
        System.out.println(RETRIES + properties1.getProperty(RETRIES));
        System.out.println(BATCH_SIZE + properties1.getProperty(BATCH_SIZE));
        System.out.println(LINGER_MS + properties1.getProperty(LINGER_MS));
        System.out.println(BUFFER_MEMORY + properties1.getProperty(BUFFER_MEMORY));
        System.out.println(STRING_KEY_SERIALIZER + properties1.getProperty(STRING_KEY_SERIALIZER));
        System.out.println(STRING_VALUE_SERIALIZER + properties1.getProperty(STRING_VALUE_SERIALIZER));
    }
}
