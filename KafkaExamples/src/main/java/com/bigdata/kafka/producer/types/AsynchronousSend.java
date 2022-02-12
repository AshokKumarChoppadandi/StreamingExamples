/**
 * This Kafka Producer sends a message to Kafka Topic and
 * it will not wait for the confirmation from the Kafka Broker / Partition Leader.
 * But this Producer takes the Callback function as a Second arguments,
 * where it get the RecordMetaData and the Exception as an Acknowledgement.
 *
 * When there is any Exception, it has to be handled in the required manner, if not ignore it.
 *
 * METHOD :: ASYNCHRONOUS SEND
 *
 * 1. This method comes in the middle of FIRE AND FORGET & SYNCHRONOUS SEND
 * 2. This method has little chance of loosing data but less when compared to FIRE & FORGET.
 * 3. Throughput / Velocity is high as it doesn't wait for the acknowledgement from Kafka Broker / Partition Leader.
 * 4. Whenever any Exception occurs we have to handle the record in the required manner.
 * 5. Out of all the three method ASYNCHRONOUS SEND plays as an efficient of producing messages in most of the cases.
 *
 * The below property is used to set the number of messages to send message before getting the Acknowledgement.
 *      max.in.flight.requests.per.connection
 * Default value is 5, can be configured.
 */
package com.bigdata.kafka.producer.types;

import com.bigdata.kafka.producer.utils.CommonUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import static com.bigdata.kafka.producer.utils.ApplicationConstants.*;

public class AsynchronousSend {
    public static void main(String[] args) {
        CommonUtils utils = new CommonUtils();

        Producer<String, String> producer = new KafkaProducer<String, String>(utils.getProperties(CONFIG_PATH));
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(EMPLOYEE_TOPIC, Integer.toString(4), "FOUR");

        producer.send(record, new MyProducerCallback());
        producer.flush();
        producer.close();
    }
}
