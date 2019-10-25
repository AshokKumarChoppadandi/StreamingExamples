package com.bigdata.kafka.producer.file.watcher;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.*;
import java.util.Properties;
import java.util.UUID;

public class LogsDirectoryWatcher implements FileAlterationListener {
    public static Producer<String, String> producer;
    public static String topicName;
    public static ProducerRecord<String, String> producerRecord;
    public static int key = 1;

    public LogsDirectoryWatcher(Properties properties, String topicName) {
        this.topicName = topicName;
        producer = new KafkaProducer<String, String>(properties);
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
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(filePath));
            String message;
            while((message = br.readLine()) != null) {
                String uniqueId = String.valueOf(UUID.randomUUID());
                producerRecord = new ProducerRecord<String, String>(topicName, Integer.toString(key), uniqueId + key + "," +message);
                producer.send(producerRecord);
                key++;
            }
            System.out.println("Data transmitted as messages from file :: " + filePath);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
}
