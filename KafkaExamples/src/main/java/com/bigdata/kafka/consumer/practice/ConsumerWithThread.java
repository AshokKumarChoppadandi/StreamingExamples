package com.bigdata.kafka.consumer.practice;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ConsumerWithThread {
    public static void main(String[] args) {
        new ConsumerWithThread().run();
    }

    public void run() {
        Logger logger = LoggerFactory.getLogger(ConsumerWithThread.class.getName());
        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "group2";
        String topic = "second-topic";

        // latch for dealing with multiple threads
        CountDownLatch latch = new CountDownLatch(1);

        System.out.println("Creating the Consumer Thread");
        logger.info("Creating the Consumer Thread");
        // Create a Consumer Runnable
        ConsumerRunnable runnable = new ConsumerRunnable(bootstrapServers, topic, groupId, latch);

        // Start the Thread
        Thread thread = new Thread(runnable);
        thread.start();

        // Add a shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            System.out.println("Caught shutdown hook");
            logger.info("Caught shutdown hook");
            runnable.shutdown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Application has exited");
            logger.info("Application has exited");
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("Application got interrupted", e);
            System.out.println("Application got interrupted" + e);
        } finally {
            logger.info("Application is closing");
            System.out.println("Application is closing");
        }
    }

    public class ConsumerRunnable implements Runnable {
        private CountDownLatch latch;
        private KafkaConsumer<String, String> consumer;
        private Logger logger = LoggerFactory.getLogger(ConsumerRunnable.class.getName());

        public ConsumerRunnable(String bootstrapServers, String topic, String groupId, CountDownLatch latch) {
            this.latch = latch;

            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            consumer = new KafkaConsumer<>(properties);
            consumer.subscribe(Collections.singletonList(topic));
        }

        @Override
        public void run() {
            try {
                while(true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                    for (ConsumerRecord<String, String> record : records) {
                        String message = "Topic :: " + record.topic() + "\n" +
                                "Partition :: " + record.partition() + "\n" +
                                "Offset :: " + record.offset() + "\n" +
                                "Key :: " + record.key() + "\n" +
                                "Value :: " + record.value() + "\n" +
                                "Timestamp :: " + record.timestamp();
                        System.out.println(message);
                        logger.info(message);
                    }
                }
            } catch (WakeupException wakeupException) {
                System.out.println("Received shutdown signal");
                logger.info("Received shutdown signal");
            } finally {
                consumer.close();
                // This tell the main that the consumer is completed
                latch.countDown();
            }
        }

        public void shutdown() {
            // wakeup() method is a special method to interrupt consumer.poll()
            // it will throw the exception - WakeUpException
            consumer.wakeup();
        }
    }
}
