package com.bigdata.kafka.producer.practice;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class SimpleProducerExample {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(SimpleProducerExample.class.getName());

        Properties properties = new Properties();

        String kafkaBrokers = "127.0.0.1:9092";
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG , "all");

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        ProducerRecord<String, String> record = new ProducerRecord<String, String>("second-topic", null, "This is the First message from Java Client");

        producer.send(record);
        producer.flush();
        producer.close();

        logger.info("Message sent successfully...!!!");
        System.out.println("Message sent successfully...!!!");
    }
}
