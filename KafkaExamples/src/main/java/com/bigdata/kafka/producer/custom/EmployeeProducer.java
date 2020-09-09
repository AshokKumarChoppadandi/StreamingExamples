package com.bigdata.kafka.producer.custom;

import com.bigdata.kafka.producer.utils.CommonUtils;
import com.bigdata.kafka.producer.utils.Employee;
import static com.bigdata.kafka.producer.utils.ApplicationConstants.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class EmployeeProducer {
    public static void main(String[] args) {
        String dob = "2019-09-04";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        CommonUtils utils = new CommonUtils();

        Producer<String, Employee> producer = null;
        ProducerRecord<String, Employee> record;
        try {
            producer = new KafkaProducer<String, Employee>(utils.getEmployeeProperties(CONFIG_PATH));
            utils.showProperties(utils.getEmployeeProperties(CONFIG_PATH));
            record = new ProducerRecord<String, Employee>("test-1", Integer.toString(2), new Employee(1, "Alice", format.parse(dob), 10000, "ACCOUNTS"));
            //record = new ProducerRecord<String, Employee>("test-1", Integer.toString(1), new Employee(1, "Alice"));

            RecordMetadata metadata = producer.send(record).get();
            System.out.println("METADATA - TOPIC :: " + metadata.topic() + ", PARTITION :: " + metadata.partition() + ", OFFSET :: " + metadata.offset());
            System.out.println("EMPLOYEE RECORD SENT SUCCESSFULLY...!!!");
        } catch (ParseException pe) {
            System.out.println("Unable to parse employee DOB : " + pe);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.flush();
            producer.close();
        }
    }
}
