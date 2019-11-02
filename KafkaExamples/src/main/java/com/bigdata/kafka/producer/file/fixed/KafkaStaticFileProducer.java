package com.bigdata.kafka.producer.file.fixed;

import com.bigdata.kafka.producer.utils.CommonUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static com.bigdata.kafka.producer.utils.ApplicationConstants.*;

public class KafkaStaticFileProducer {
    public static void main(String[] args) throws IOException {
        String topicName = "console-test";
        CommonUtils utils = new CommonUtils();
        Properties properties = utils.getProperties(CONFIG_PATH);

        Producer<String, String> producer = new KafkaProducer<String, String>(properties);
        ProducerRecord<String, String> record;

        BufferedReader br = new BufferedReader(new FileReader(TEST_FILE));
        String line;
        int lineNumber = 1;
        while ((line = br.readLine()) != null) {
            record = new ProducerRecord<String, String>(topicName, Integer.toString(lineNumber), line);
            producer.send(record);
        }
        System.out.println("File data transferred as Messages...!!!");

        producer.close();

    }
}
