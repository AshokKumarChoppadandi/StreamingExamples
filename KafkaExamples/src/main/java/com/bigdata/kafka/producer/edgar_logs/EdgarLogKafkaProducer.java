package com.bigdata.kafka.producer.edgar_logs;

import static com.bigdata.kafka.producer.utils.ApplicationConstants.*;
import com.bigdata.kafka.producer.utils.CommonUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class EdgarLogKafkaProducer {
    public static void main(String[] args) throws Exception {
        String configFilePath = args[0];

        CommonUtils utils = new CommonUtils();
        Properties properties = utils.getProperties(configFilePath);
        printProperties(properties);
        final File directory = new File(properties.getProperty(INPUT_LOGS_DIR));
        FileAlterationObserver observer = new FileAlterationObserver(directory);
        observer.addListener(new EdgarLogsDirectoryWatcher(properties));

        final FileAlterationMonitor monitor = new FileAlterationMonitor(POLLING_INTERVAL);
        monitor.addObserver(observer);


        final CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread("edgar-kafka-producer-shutdown-hook") {
            @Override
            public void run() {
                try {
                    System.out.println("Shutting down the Directory watcher");
                    monitor.stop();
                    latch.countDown();
                } catch (Exception e) {
                    System.out.println("Exception occurred while shutting down the Directory Watcher.");
                    e.printStackTrace();
                }
            }
        });

        try {
            monitor.start();
            latch.await();
        } catch (InterruptedException e) {
            System.out.println("Received signal to shutdown the Directory Watcher.");
        }
    }

    public static void printProperties(Properties properties) {
        System.out.println("Printing the Application Properties : ");
        PrintWriter writer = new PrintWriter(System.out);
        properties.list(writer);
        writer.flush();
        writer.close();
    }
}
