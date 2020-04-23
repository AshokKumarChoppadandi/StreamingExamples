package com.bigdata.kafka.producer;

import com.bigdata.kafka.models.UsedCar;
import com.bigdata.kafka.serializer.UsedCarSerializer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class UsedCarProducer {
    public static void main(String[] args) {
        String topicName = "used-cars";
        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, UsedCarSerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 1);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);

        Producer<String, UsedCar> producer = new KafkaProducer<>(properties);
        ProducerRecord<String, UsedCar> producerRecord = new ProducerRecord<>(topicName, Integer.toString(1), getUsedCarRecord());

        producer.send(producerRecord, (recordMetadata, e) -> {
            if(e != null) {
                e.printStackTrace();
            } else {
                System.out.println(String.format("Record sent successfully to topic: %s", topicName));
            }
        });
    }

    public static UsedCar getUsedCarRecord() {
        /**
         * ford
         * galaxy
         * 151000
         * 2011
         * 2000
         * 103
         *
         *
         * None
         * man
         * 5
         * 7
         * diesel
         * 2015-11-14 18:10:06.838319+00
         * 2016-01-27 20:40:15.46361+00
         * 10584.75
         */

        return new UsedCar(
                "ford",
                "galaxy",
                151000,
                2011,
                "2000",
                103,
                "",
                "",
                0,
                "man",
                "5",
                7,
                "diesel",
                "2015-11-14 18:10:06.838319+00",
                "2016-01-27 20:40:15.46361+00",
                10584.75
        );
    }
}
