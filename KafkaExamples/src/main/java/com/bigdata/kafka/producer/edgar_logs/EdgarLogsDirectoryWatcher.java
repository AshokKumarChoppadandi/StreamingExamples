package com.bigdata.kafka.producer.edgar_logs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import static com.bigdata.kafka.producer.utils.ApplicationConstants.*;

public class EdgarLogsDirectoryWatcher implements FileAlterationListener {
    public Producer<String, String> producer;
    public ProducerRecord<String, String> producerRecord = null;
    public KafkaProducer<String, GenericRecord> avroProducer;
    public ProducerRecord<String, GenericRecord> avroRecord = null;

    public String topicName;
    public Properties properties;

    public EdgarLogsDirectoryWatcher(Properties properties) {
        this.topicName = properties.getProperty(TOPIC_NAME);
        this.properties = properties;
        String outputFormat = properties.getProperty(OUTPUT_FORMAT, DEFAULT_OUTPUT_FORMAT);
        if(outputFormat.equalsIgnoreCase(AVRO_OUTPUT_FORMAT)) {
            this.avroProducer = new KafkaProducer<>(properties);
        } else {
            this.producer = new KafkaProducer<>(properties);
        }
    }

    public void onStart(FileAlterationObserver fileAlterationObserver) {
        //System.out.println("The File / Directory watcher has started on directory :: " + fileAlterationObserver.getDirectory().getAbsolutePath());
    }

    public void onDirectoryCreate(File file) {
        //System.out.println(file.getAbsolutePath() + " Directory is created");
    }

    public void onDirectoryChange(File file) {
        //System.out.println(file.getAbsolutePath() + " Directory is modified");
    }

    public void onDirectoryDelete(File file) {
        //System.out.println(file.getAbsolutePath() + " Directory is deleted");
    }

    public void onFileCreate(File file) {
        System.out.println(file.getAbsolutePath() + " File is created");
        String filePath = file.getAbsolutePath();
        Scanner scanner = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(filePath);
            scanner = new Scanner(fileInputStream);
            String outputFormat = properties.getProperty(OUTPUT_FORMAT);
            switch (outputFormat) {
                case "avro":
                    System.out.println("Producing Messages in the form of AVRO");
                    generateAvroMessages(scanner);
                    break;
                case "json":
                    System.out.println("Producing Messages in the form of JSON");
                    generateJsonMessages(scanner);
                    break;
                default:
                    System.out.println("Producing Messages in the form of CSV");
                    generateCSVMessages(scanner);
                    break;
            }


            System.out.println("Data transmitted as messages from file :: " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(scanner != null) {
                scanner.close();
            }
        }
    }

    public void onFileChange(File file) {
        //System.out.println(file.getAbsolutePath() + " File is modified");
    }

    public void onFileDelete(File file) {
        //System.out.println(file.getAbsolutePath() + " File is deleted");
    }

    public void onStop(FileAlterationObserver fileAlterationObserver) {
        System.out.println("File / Directory watcher is stopped on " + fileAlterationObserver.getDirectory().getAbsolutePath());
    }

    public void generateCSVMessages(Scanner scanner) {
        while(scanner.hasNextLine()) {
            producerRecord = new ProducerRecord<>(topicName, null, scanner.nextLine());
            producer.send(producerRecord);
        }
        producer.flush();
    }

    public void generateJsonMessages(Scanner scanner) {
        ObjectMapper mapper = new ObjectMapper();
        EdgarLog edgarLog;
        String rawMessage;
        String[] messageFields;
        while(scanner.hasNextLine()) {
            rawMessage = scanner.nextLine();
            messageFields = rawMessage.split(",");
            edgarLog = new EdgarLog(messageFields);
            try {
                producerRecord = new ProducerRecord<>(topicName, null, mapper.writeValueAsString(edgarLog));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            producer.send(producerRecord);
        }
        producer.flush();
    }

    private void generateAvroMessages(Scanner scanner) {
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        Schema.Parser parser = new Schema.Parser();
        Schema schema;
        try {
            schema = parser.parse(new File(properties.getProperty(SCHEMA_FILE)));
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse or found Avro Schema File...\nExiting the job.");
        }

        EdgarLog edgarLog;
        String rawMessage;
        String[] messageFields;

        while(scanner.hasNextLine()) {
            GenericRecord genericRecord = new GenericData.Record(schema);
            rawMessage = scanner.nextLine();
            messageFields = rawMessage.split(",");
            edgarLog = new EdgarLog(messageFields);
            genericRecord.put("ip", edgarLog.getIpAddress());
            genericRecord.put("date", edgarLog.getDate());
            genericRecord.put("time", edgarLog.getTime());
            genericRecord.put("zone", edgarLog.getZone());
            genericRecord.put("cik", edgarLog.getCik());
            genericRecord.put("accession", edgarLog.getAccession());
            genericRecord.put("extention", edgarLog.getExtention());
            genericRecord.put("code", edgarLog.getCode());
            genericRecord.put("size", edgarLog.getSize());
            genericRecord.put("idx", edgarLog.getIdx());
            genericRecord.put("norefer", edgarLog.getNoRefer());
            genericRecord.put("noagent", edgarLog.getNoAgent());
            genericRecord.put("find", edgarLog.getFind());
            genericRecord.put("crawler", edgarLog.getCrawler());
            genericRecord.put("browser", edgarLog.getBrowser());

            avroRecord = new ProducerRecord<>(topicName, null, genericRecord);
            avroProducer.send(avroRecord);
        }

        avroProducer.flush();
    }
}
