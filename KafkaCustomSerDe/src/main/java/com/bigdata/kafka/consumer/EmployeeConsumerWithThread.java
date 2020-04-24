package com.bigdata.kafka.consumer;

import java.util.concurrent.CountDownLatch;

public class EmployeeConsumerWithThread {
    public static void main(String[] args) {
        new EmployeeConsumerWithThread().run();
    }

    private EmployeeConsumerWithThread() {}

    private void run() {
        CountDownLatch latch = new CountDownLatch(1);
        String bootStrapServers = "localhost:9092";
        String topic = "employee";
        String groupId = "employee-consumer-group-2";
        ConsumerRunnable runnable = new ConsumerRunnable(bootStrapServers, topic, groupId, latch);

        Thread thread = new Thread(runnable);
        thread.start();

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            runnable.shutdown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Application Stopped Successfully...!!!");

        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

        }
    }


}
