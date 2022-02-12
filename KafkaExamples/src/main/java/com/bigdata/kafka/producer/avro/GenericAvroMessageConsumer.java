package com.bigdata.kafka.producer.avro;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * java -cp \
 *   IdeaProjects/Kafka-Streams-Examples/target/Kafka-Streams-Examples-1.0-SNAPSHOT.jar \
 *   com.bigdata.kafka.streams.AvroMessageConsumer \
 *   broker:9092 \
 *   orders \
 *   group1
 */

public class GenericAvroMessageConsumer {
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

        KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(topic));

        try{
            while (true) {
                ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, GenericRecord> record : records) {
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
