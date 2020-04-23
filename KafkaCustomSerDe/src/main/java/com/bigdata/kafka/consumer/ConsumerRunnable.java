package com.bigdata.kafka.consumer;

import com.bigdata.kafka.deserializer.EmployeeDeserializer;
import com.bigdata.kafka.models.Employee;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ConsumerRunnable implements Runnable {
    private CountDownLatch latch;
    private KafkaConsumer<String, Employee> consumer;
    private String topicName;

    public ConsumerRunnable(String bootstrapServers, String topicName, String groupId, CountDownLatch latch) {
        Properties properties = new Properties();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EmployeeDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);

        this.consumer = new KafkaConsumer<>(properties);
        this.latch = latch;
        this.topicName = topicName;
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(Collections.singletonList(this.topicName));
            while (true) {
                ConsumerRecords<String, Employee> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, Employee> record : records) {
                    System.out.println(
                            "Topic :: " + record.topic() + "," +
                                    "Partition :: " + record.partition() + "," +
                                    "Offset :: " + record.offset() + "," +
                                    "Timestamp :: " + record.timestamp() + "," +
                                    record.value()
                    );
                }
            }
        } catch (WakeupException e) {
            //e.printStackTrace();
        } finally {
            consumer.close();
            latch.countDown();
        }
    }

    public void shutdown() {
        consumer.wakeup();
    }
}
