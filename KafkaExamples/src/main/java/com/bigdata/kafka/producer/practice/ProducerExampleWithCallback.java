package com.bigdata.kafka.producer.practice;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerExampleWithCallback {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(ProducerExampleWithCallback.class);
        Properties properties = new Properties();

        String topicName = "second-topic";
        String bootstrapServers = "127.0.0.1:9092";
        String value = "This is the message from Java Client with Callback";
        Integer key = 106;
        String acks = "all";

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, acks);

        KafkaProducer<Integer, String> producer = new KafkaProducer<>(properties);
        ProducerRecord<Integer, String> record = new ProducerRecord<>(topicName, key, value);

        producer.send(record, (recordMetadata, e) -> {
            if(e == null) {
                String logMessage = "Message sent successfully...!!!\n" +
                        "TopicName :: " + recordMetadata.topic() + "\n" +
                        "Partition :: " + recordMetadata.partition() + "\n" +
                        "Offset :: " + recordMetadata.offset() + "\n" +
                        "Timestamp :: " + recordMetadata.offset();

                System.out.println(logMessage);
                logger.info(logMessage);
            }
        });

        producer.close();
    }
}
