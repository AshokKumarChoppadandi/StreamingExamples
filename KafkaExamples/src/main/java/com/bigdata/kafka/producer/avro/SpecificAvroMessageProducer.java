package com.bigdata.kafka.producer.avro;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class SpecificAvroMessageProducer {
    public static void main(String[] args) {
        String bootstrapServers = args[0];
        String topic = args[1];

        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        // properties.put("schema.registry.url", "http://schemaregistry:8081");
        properties.put("schema.registry.url", "http://localhost:8081");

        KafkaProducer<String, SpecificRecord> producer = new KafkaProducer<>(properties);

        Orders orderItem = Orders
                .newBuilder()
                .setId(123)
                .setName("Ashok Kumar Choppadandi")
                .setPrice(25.5f)
                .setQuantity(3)
                .build();

        ProducerRecord<String, SpecificRecord> record = new ProducerRecord<>(topic, null, orderItem);
        try {
            producer.send(record);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            producer.flush();
            producer.close();
        }
    }
}

