package com.bigdata.kafka.streams;

import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * java -cp \
 *   IdeaProjects/Kafka-Streams-Examples/target/Kafka-Streams-Examples-1.0-SNAPSHOT.jar \
 *   com.bigdata.kafka.streams.TwitterStreamsApp \
 *   EmployeesNamesAvro \
 *   broker:9092 \
 *   employees \
 *   employees-names-avro
 */

public class TwitterStreamsApp {
    public static void main(String[] args) throws IOException {
        String appName = args[0];
        String bootstrapServers = args[1];
        String inputTopic = args[2];
        String outputTopic = args[3];

        final Schema schema = new Schema.Parser().parse(
                EmployeesAvroStreamsApp.class.getResourceAsStream("/TweetFiltered.avsc")
        );

        Properties properties = new Properties();

        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, appName);
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
        properties.put("schema.registry.url", "http://schemaregistry:8081");

        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url", "http://schemaregistry:8081");
        final Serde<GenericRecord> valueGenericAvroSerde = new GenericAvroSerde();
        valueGenericAvroSerde.configure(serdeConfig, false);

        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, GenericRecord> inputStream = builder.stream(
                inputTopic,
                Consumed.with(Serdes.String(), valueGenericAvroSerde)
        );

        final KStream<String, GenericRecord> outputStream = inputStream.mapValues(x -> {
            GenericRecord genericRecord = new GenericData.Record(schema);

            genericRecord.put("created_date", Long.parseLong(x.get(0).toString()));
            genericRecord.put("id", Long.parseLong(x.get(1).toString()));
            genericRecord.put("tweet", x.get(2).toString());
            genericRecord.put("tweet_source", x.get(2).toString());
            genericRecord.put("retweet_count", Integer.parseInt(x.get(16).toString()));
            genericRecord.put("language", x.get(20).toString());
            // genericRecord.put("user_id", x.get("user"));
            return genericRecord;
        });
        // outputStream.to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));
        outputStream.to(outputTopic, Produced.with(Serdes.String(), valueGenericAvroSerde));
        outputStream.print(Printed.toSysOut());


        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, properties);

        final CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread("streams-word-count-shutdown-hoot") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (InterruptedException e) {
            System.out.println("Exception Occurred...!!!\n" + e);
            e.printStackTrace();
        }
    }
}
