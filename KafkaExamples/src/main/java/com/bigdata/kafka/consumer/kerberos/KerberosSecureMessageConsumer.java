package com.bigdata.kafka.consumer.kerberos;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KerberosSecureMessageConsumer {
    public static void main(String[] args) {
        String topicName = args[0];
        Properties properties = new Properties();

        String bootstrapServer = "SASL_SSL://worker1.bigdata.com:9094,SASL_SSL://worker2.bigdata.com:9094,SASL_SSL://worker3.bigdata.com:9094";

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-1");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // SSL Properties
        properties.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "/home/bigdata/ssl_client/kafka.client.keystore.jks");
        properties.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "clientpassword");
        properties.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "clientpassword");
        properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "/home/bigdata/ssl_client/kafka.client.truststore.jks");
        properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "clientpassword");

        // Kerberos Properties
        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        properties.put(SaslConfigs.SASL_KERBEROS_SERVICE_NAME, "kafka");

        Consumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(topicName));

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
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
