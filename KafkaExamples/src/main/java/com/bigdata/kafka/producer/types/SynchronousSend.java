/**
 * This Kafka Producer sends a message to Kafka Topic and
 * it will wait for the confirmation from the Kafka Broker / Partition Leader for every message.
 *
 * METHOD :: SYNCHRONOUS SEND
 *
 * 1. This method can be chosen when the data loss is critical.
 * 2. Decreases the velocity / speed of the producing the messages because it has to wait for every single confirmation.
 * 3. Next message cannot be produced until the acknowledgement of the current message comes from Broker / Partition leader.
 */
package com.bigdata.kafka.producer.types;

import com.bigdata.kafka.producer.utils.CommonUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.bigdata.kafka.producer.utils.ApplicationConstants.*;
public class SynchronousSend {
    public static void main(String[] args) {
        CommonUtils utils = new CommonUtils();

        Producer<String, String> producer = new KafkaProducer<String, String>(utils.getProperties(CONFIG_PATH));
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(EMPLOYEE_TOPIC, Integer.toString(3), "THREE");

        try {
            Future<RecordMetadata> response = producer.send(record);
            RecordMetadata recordMetadata = response.get();
            System.out.println("MESSAGE SENT TO PARTITION :: " + recordMetadata.partition() + ", OFFSET :: " + recordMetadata.offset());
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("SYNCHRONOUS SEND FAILED WITH AN INTERRUPTED EXCEPTION...!!!");
        } catch (ExecutionException e) {
            e.printStackTrace();
            System.out.println("SYNCHRONOUS SEND FAILED WITH AN EXECUTION EXCEPTION...!!!");
        }
    }
}
