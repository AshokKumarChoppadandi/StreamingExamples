package com.bigdata.kafka.producer.console;

import com.bigdata.kafka.producer.utils.CommonUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import static com.bigdata.kafka.producer.utils.ApplicationConstants.CONFIG_PATH;

public class KafkaConsoleProducer {
    public static void main(String[] args) throws IOException {
        String topicName = "console-test";
        CommonUtils utils = new CommonUtils();
        Properties properties = utils.getProperties(CONFIG_PATH);

        utils.showProperties(properties);
        Producer<String, String> producer = new KafkaProducer<String, String>(properties);
        ProducerRecord<String, String> record;

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String message;
        int key = 1;
        System.out.println("Start Sending Some Messages to Topic :: " + topicName);
        while (true) {
            message = br.readLine();
            if(message.equalsIgnoreCase(":stop")) {
                System.out.println("Thank You for Sending Messages");
                break;
            }
            record = new ProducerRecord<String, String>(topicName, Integer.toString(key), message);
            producer.send(record);
            System.out.println("Message Sent :: \nTopic = " + topicName + ", Key = " + key + ", Message :: " + message);
            key++;
        }
        producer.close();
        System.out.println("Kafka Producer Stopped Successfully...!!!");

    }
}
