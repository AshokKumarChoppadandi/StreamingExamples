package com.bigdata.spark.structured.streaming;

import com.bigdata.spark.SparkSessionInitializer;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import com.bigdata.spark.structured.streaming.JavaUtils;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;

public class ReadKafkaTopic {
    public static void main(String[] args) throws StreamingQueryException {
        String appName = "READ KAFKA TOPIC USING JAVA";
        SparkSession spark = SparkSessionInitializer.getSparkSession(appName);

        Dataset<Row> messages = spark
                .readStream()
                .format(JavaUtils.KAFKA)
                .option(JavaUtils.BOOTSTRAP_SERVERS, JavaUtils.KAFKA_BROKERS_LIST)
                //.option(JavaUtils.SUBSCRIBE, JavaUtils.TEST_TOPIC)
                .option(JavaUtils.SUBSCRIBE, JavaUtils.TEST_TOPIC + "-with-schema")
                .option(JavaUtils.STARTING_OFFSETS, JavaUtils.LATEST)
                .load();

        System.out.println("IS STREAMING :: " + messages.isStreaming());

        StreamingQuery query0 = messages.writeStream().outputMode(JavaUtils.UPDATE).format(JavaUtils.CONSOLE).start();

        Dataset<Row> messages2 = messages.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)");
        StreamingQuery query = messages2.writeStream().outputMode(JavaUtils.UPDATE).format(JavaUtils.CONSOLE).start();

        query0.awaitTermination();
        query.awaitTermination();
    }
}
