package com.bigdata.kafka.producer.twitter;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TwitterProducer {

    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;

    private String bootstrapServer = "localhost:9092";
    private String topicName = "twitter-tweets";
    private String acks = "all";
    private String twitterClientName = "Twitter-Client";

    private static Logger logger = LoggerFactory.getLogger(TwitterProducer.class.getName());

    public TwitterProducer(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
    }

    public static void main(String[] args) {
        String consumerKey = args[0];
        String consumerSecretKey = args[1];
        String accessToken = args[2];
        String accessTokenSecret = args[3];

        logger.info("Starting Twitter Producer...!!!");
        new TwitterProducer(consumerKey, consumerSecretKey, accessToken, accessTokenSecret).run(args);
    }

    public void run(String[] args) {

        /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        logger.info("Setting up the Queue to read the tweets.");
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<>(1000);

        /** Create a Twitter Client */
        logger.info("Creating the Twitter Client.");
        Client twitterClient = createTwitterClient(msgQueue, Arrays.asList("#India", "#IndianArmy"));

        logger.info("Connecting to Twitter Client");
        twitterClient.connect();

        /** Create a Kafka Producer */
        KafkaProducer<String, String> kafkaProducer = createKafkaProducer(bootstrapServer);
        ProducerRecord<String, String> producerRecord;

        /** Loop to send tweets to Kafka */
        logger.info("Reading Tweets...");
        while (!twitterClient.isDone()) {
            try {
                String message = msgQueue.poll(5, TimeUnit.SECONDS);
                producerRecord = new ProducerRecord<>(topicName, null, message);
                kafkaProducer.send(producerRecord, (recordMetadata, e) -> {
                    if(e != null) {
                        logger.error("Something bad happened : ", e);
                    }
                });
                logger.info(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
                twitterClient.stop();
            }
        }
    }

    public Client createTwitterClient(BlockingQueue<String> messageQueue, List<String> keyTerms) {

        Hosts hosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        List<String> terms = Lists.newArrayList(keyTerms);

        endpoint.trackTerms(terms);

        Authentication authentication = new OAuth1(
                consumerKey,
                consumerSecret,
                accessToken,
                accessTokenSecret
        );

        ClientBuilder builder = new ClientBuilder()
                .name(twitterClientName)
                .hosts(hosts)
                .authentication(authentication)
                .endpoint(endpoint)
                .processor(new StringDelimitedProcessor(messageQueue));

        return builder.build();
    }

    public KafkaProducer<String, String> createKafkaProducer(String bootstrapServers) {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, acks);

        return new KafkaProducer<>(properties);
    }
}
