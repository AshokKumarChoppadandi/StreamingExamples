package com.bigdata.kafka.streams;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * java -cp \
 *   IdeaProjects/Kafka-Streams-Examples/target/Kafka-Streams-Examples-1.0-SNAPSHOT.jar \
 *   com.bigdata.kafka.streams.AvroMessageProducer \
 *   broker:9092 \
 *   orders
 */

public class AvroMessageProducer {
    public static void main(String[] args) {

        String bootstrapServers = args[0];
        String topic = args[1];

        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        properties.put("schema.registry.url", "http://schemaregistry:8081");

        KafkaProducer<String, GenericRecord> producer = new KafkaProducer<>(properties);

        String orderSchema = "{\n" +
                "  \"type\": \"record\",\n" +
                "  \"name\": \"orders\",\n" +
                "  \"fields\": [\n" +
                "    {\"name\": \"id\", \"type\": \"int\"},\n" +
                "    {\"name\": \"name\", \"type\": \"string\"},\n" +
                "    {\"name\": \"price\", \"type\": \"double\"},\n" +
                "    {\"name\": \"quantity\", \"type\": \"int\"}\n" +
                "  ]\n" +
                "}";

        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(orderSchema);

        GenericRecord genericRecord = new GenericData.Record(schema);
        genericRecord.put("id", 123);
        genericRecord.put("name", "Order123");
        genericRecord.put("price", 10.25);
        genericRecord.put("quantity", 3);

        ProducerRecord<String, GenericRecord> record = new ProducerRecord<>(topic, null, genericRecord);
        try {
            producer.send(record);
        } catch (SerializationException ex) {
            ex.printStackTrace();
        } finally {
            producer.flush();
            producer.close();
        }
    }
}
