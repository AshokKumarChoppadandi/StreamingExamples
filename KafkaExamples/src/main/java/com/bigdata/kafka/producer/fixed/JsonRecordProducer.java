package com.bigdata.kafka.producer.fixed;

import com.bigdata.kafka.producer.utils.CommonUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.internals.FutureRecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class JsonRecordProducer {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String topicName = "test-with-schema";
        String configPath = "./src/main/resources/kafka/config.properties";

        CommonUtils utils = new CommonUtils();
        Properties properties = utils.getProperties(configPath);

        Producer<String, String> producer = new KafkaProducer<String, String>(properties);
        //String message = "{\"id\":1,\"name\":\"Spark\",\"version\":2.4,\"description\":\"Apache Spark is a Distributed Computing Engine\"}";
        String message = "{\"id\":2,\"name\":\"Hadoop\",\"version\":2.7,\"description\":\"Apache Hadoop is a Distributed Storage and Processing Framework\"}";

        ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicName, Integer.toString(17), message);
        RecordMetadata metadata = producer.send(record).get();

        if(metadata.hasOffset()) {
            //RecordMetadata metadata = res.get(
            String topic = metadata.topic();
            int partition = metadata.partition();
            long offset = metadata.hasOffset() ? metadata.offset() : null;
            String metadataString = metadata.toString();
            System.out.println("topic :: " + topic);
            System.out.println("partition :: " + partition);
            System.out.println("offset :: " + offset);
            System.out.println("metadataString :: " + metadataString);
            System.out.println("SUCCESS: MESSAGE SENT SUCCESSFULLY TO TOPIC - " + topicName);
        } else {
            //RecordMetadata test =  metadata.
            System.out.println(metadata.toString());
            System.out.println("ERROR: MESSAGE NOT SENT TO TOPIC - " + topicName);
        }

        producer.close();


    }
}
