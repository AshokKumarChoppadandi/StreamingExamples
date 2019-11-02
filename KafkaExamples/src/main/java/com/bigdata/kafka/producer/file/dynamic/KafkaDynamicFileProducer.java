package com.bigdata.kafka.producer.file.dynamic;

import com.bigdata.kafka.producer.file.watcher.LogsDirectoryWatcher;
import com.bigdata.kafka.producer.utils.CommonUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.Properties;

import static com.bigdata.kafka.producer.utils.ApplicationConstants.*;

public class KafkaDynamicFileProducer {

    private final static long pollingInterval = 10 * 1000;

    public static void main(String[] args) throws Exception {
        String topicName = "used_cars";
        CommonUtils utils = new CommonUtils();
        Properties properties = utils.getProperties(CONFIG_PATH);
        final File directory = new File(CARS_INPUT_DIRECTORY);
        FileAlterationObserver observer = new FileAlterationObserver(directory);
        observer.addListener(new LogsDirectoryWatcher(properties, topicName));

        final FileAlterationMonitor monitor = new FileAlterationMonitor(pollingInterval);
        monitor.addObserver(observer);
        monitor.start();
        System.out.println("Starting Monitor on " + directory.getAbsolutePath() + "\nPress ctrl + c to stop monitoring...!!!");
    }
}
