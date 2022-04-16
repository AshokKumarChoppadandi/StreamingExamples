package com.bigdata.kafka.consumer.kerberos;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KerberosSecureAvroMessageConsumer {
    public static void main(String[] args) {
        String topicName = args[0];
        Properties properties = new Properties();

        String bootstrapServer = "SASL_SSL://worker1.bigdata.com:9094,SASL_SSL://worker2.bigdata.com:9094,SASL_SSL://worker3.bigdata.com:9094";
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-2");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);

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

        Consumer<String, SpecificRecord> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(topicName));

        try {
            while (true) {
                ConsumerRecords<String, SpecificRecord> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, SpecificRecord> record : records) {
                    System.out.println(record.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }

        System.out.println("Message Read Successfully!!!");
    }
}
