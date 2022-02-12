package com.bigdata.kafka.producer.avro;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class SpecificAvroMessageConsumer {
    public static void main(String[] args) {
        String bootstrapServers = args[0];
        String topic = args[1];
        String consumerGroup = args[2];

        Properties properties = new Properties();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        // properties.put("schema.registry.url", "http://schemaregistry:8081");
        properties.put("schema.registry.url", "http://localhost:8081");

        // KafkaConsumer<String, Orders> consumer = new KafkaConsumer<>(properties);
        KafkaConsumer<String, SpecificRecord> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(topic));

        try{
            while (true) {
                // ConsumerRecords<String, Orders> records = consumer.poll(Duration.ofMillis(100));
                ConsumerRecords<String, SpecificRecord> records = consumer.poll(Duration.ofMillis(100));
                // for (ConsumerRecord<String, Orders> record : records) {
                for (ConsumerRecord<String, SpecificRecord> record : records) {
                    System.out.println(record.toString());
                }
            }
        } catch (Exception ex ) {
            ex.printStackTrace();
        } finally {
            consumer.close();
        }

    }
}
