package com.bigdata.spark.structured.streaming;

import com.bigdata.spark.SparkSessionInitializer;
import static org.apache.spark.sql.functions.from_json;
import static org.apache.spark.sql.functions.col;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import com.bigdata.spark.structured.streaming.JavaUtils;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.List;

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
                .option(JavaUtils.STARTING_OFFSETS, JavaUtils.EARLIEST)
                .load();

        System.out.println("IS STREAMING :: " + messages.isStreaming());

        List<StructField> fields = new ArrayList<>();
        fields.add(DataTypes.createStructField("id", DataTypes.IntegerType, true));
        fields.add(DataTypes.createStructField("name", DataTypes.StringType, true));
        fields.add(DataTypes.createStructField("version", DataTypes.DoubleType, true));
        fields.add(DataTypes.createStructField("description", DataTypes.StringType, true));

        StructType schema = DataTypes.createStructType(fields);
        Dataset<Row> messages2 = messages.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)");

        Dataset<Row> messages3 = messages2.select(from_json(col("value"), schema).as("temp"));
        Dataset<Row> messages4 = messages3.select("temp.*");
        messages4.printSchema();

        StreamingQuery query = messages4.writeStream().outputMode(JavaUtils.UPDATE).format(JavaUtils.CONSOLE).start();
        //StreamingQuery query1 = messages4.writeStream().foreachBatch()

        query.awaitTermination();
        //query1.awaitTermination();
    }
}
