package com.bigdata.kafka.streams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class StreamPipeExample {
    public static void main(String[] args) {
        // 1. Setup the Properties.
        Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "WordCount");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // 2. Stream Builder.
        final StreamsBuilder builder = new StreamsBuilder();

        // 3. KStream
        final KStream<String, String> sourceStream = builder.stream("streams-input1");

        final String prefix = "PREFIX";

        // 4. Transformation and Writing the result to Output kafka Stream.
        sourceStream.mapValues(stream -> String.join(" :: ", prefix, stream)).to("streams-output1");

        // 5. Topology
        final Topology topology = builder.build();

        // 6. KafkaStreams
        final KafkaStreams streams = new KafkaStreams(topology, props);

        // 7. CountDownLatch
        final CountDownLatch latch = new CountDownLatch(1);

        // 8. Adding Shutdown hook.
        Runtime.getRuntime().addShutdownHook(
                new Thread("streams-shutdown-hoot") {
                    @Override
                    public void run() {
                        streams.close();
                        latch.countDown();
                    }
                }
        );

        // 8. Adding Shutdown hook using Method Reference (Java 8 Feature)
        //Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        try {
            // 9. Starting the Stream Processing.
            streams.start();
            // Printing the Topology.
            System.out.println("Topology :: \n" + streams.toString());
            latch.await();
        } catch (Exception ex) {
            System.out.println("Exception Occurred...!!!\n" + ex);
            ex.printStackTrace();
        }

        System.out.println("Streams Program Completed Successfully...!!!");
    }
}
