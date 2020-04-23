package com.bigdata.kafka.producer;

import com.bigdata.kafka.models.Employee;
import com.bigdata.kafka.serializer.EmployeeSerializer;
import com.bigdata.kafka.serializer.GenericSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class EmployeeProducer {
    public static void main(String[] args) {
        Properties properties = new Properties();

        String topicName = "employee";

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EmployeeSerializer.class.getName());
        //properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GenericSerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 1);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);

        Producer<String, Employee> producer = new KafkaProducer<>(properties);
        ProducerRecord<String, Employee> producerRecord = new ProducerRecord<>(topicName, Integer.toString(4), new Employee(104, "TEST4", 40000, "IT"));

        producer.send(producerRecord);

        producer.flush();
        producer.close();
    }
}
