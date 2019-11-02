/**
 * This is a Simple Kafka Producer which sends a message to Kafka Topic and
 * it will not wait for any confirmation from the Kafka Broker.
 *
 * METHOD :: FIRE AND FORGET
 *
 * 1. This method can be chosen when little amount of data loss is accepted.
 * 2. Very Fast because it didn't wait for any confirmation, just keep on sending messages.
 * 3. Simple in implementation
 */
package com.bigdata.kafka.producer.types;

import com.bigdata.kafka.producer.utils.CommonUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import static com.bigdata.kafka.producer.utils.ApplicationConstants.*;

public class KafkaSimpleProducer {
    public static void main(String[] args) {
        CommonUtils utils = new CommonUtils();
        Properties properties = utils.getProperties(CONFIG_PATH);

        Producer<String, String> producer = new KafkaProducer<String, String>(properties);
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(EMPLOYEE_TOPIC, Integer.toString(2), "TWO");

        producer.send(record);
        System.out.println("MESSAGE SENT SUCCESSFULLY...!!!");

        producer.flush();
        producer.close();
    }
}
