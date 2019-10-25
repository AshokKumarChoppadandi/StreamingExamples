package com.bigdata.kafka.producer.fixed;

import com.bigdata.kafka.producer.utils.CommonUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaStaticMessagesProducer {
    public static void main(String[] args) {
        String topicName = "console-test";
        String configPath = "./src/main/resources/kafka/config.properties";

        CommonUtils utils = new CommonUtils();
        Properties properties = utils.getProperties(configPath);

        Producer<String, String> producer = new KafkaProducer<String, String>(properties);
        String message;
        for(int i = 1; i <= 10; i++) {
            message = "This is message " + i;
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicName, Integer.toString(i), message);
            producer.send(record);
        }
        System.out.println("10 Messages sent successfully to topic " + topicName);
        producer.close();
    }
}
