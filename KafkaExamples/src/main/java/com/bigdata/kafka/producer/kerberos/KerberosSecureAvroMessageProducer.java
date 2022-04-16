package com.bigdata.kafka.producer.kerberos;

import com.bigdata.kafka.employee.Employee;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KerberosSecureAvroMessageProducer {
    public static void main(String[] args) {
        String topicName = args[0];
        Properties properties = new Properties();

        String bootstrapServer = "SASL_SSL://worker1.bigdata.com:9094,SASL_SSL://worker2.bigdata.com:9094,SASL_SSL://worker3.bigdata.com:9094";
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, "0");
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        properties.put(ProducerConfig.LINGER_MS_CONFIG, "1");
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        // SSL Properties
        properties.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "/home/bigdata/ssl_client/kafka.client.keystore.jks");
        properties.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "clientpassword");
        properties.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "clientpassword");
        properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/home/bigdata/ssl_client/kafka.client.truststore.jks");
        properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "clientpassword");

        // Kerberos Properties
        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        properties.put(SaslConfigs.SASL_KERBEROS_SERVICE_NAME, "kafka");

        // Schema Registry Properties
        properties.put("schema.registry.url", "http://192.168.0.112:8081");

        Employee.Builder builder = Employee.newBuilder();

        builder.setEid(123);
        builder.setEname("Alice");
        builder.setEage(25);
        builder.setEsalary(12345);
        builder.setEdept("ABC");

        Employee employee = builder.build();

        Producer<String, SpecificRecord> producer = new KafkaProducer<>(properties);
        ProducerRecord<String, SpecificRecord> record = new ProducerRecord<>(topicName, null, employee);

        producer.send(record);
        producer.flush();
        producer.close();

        System.out.println("Message Sent Successfully!!!");
    }
}
