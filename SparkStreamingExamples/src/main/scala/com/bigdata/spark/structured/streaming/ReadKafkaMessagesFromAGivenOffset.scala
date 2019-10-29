package com.bigdata.spark.structured.streaming

import com.bigdata.spark.SparkSessionInitializer
import com.bigdata.spark.models.KafkaOffset
import com.bigdata.spark.streaming.utils.DBUtils
import com.bigdata.spark.structured.streaming.ScalaUtils.{BOOTSTRAP_SERVERS, EARLIEST, KAFKA, KAFKA_BROKERS_LIST, STARTING_OFFSETS, SUBSCRIBE}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, from_json, lit, max}
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructField, StructType}

object ReadKafkaMessagesFromAGivenOffset {
  def main(args: Array[String]): Unit = {
    val appName = "Read Kafka Messages From A Given Offset"
    val topicName = "test-with-schema"

    val startingOffsets = DBUtils.readKafkaOffsetFromDBAsJsonString("test-with-schema")
    val spark = SparkSessionInitializer.getSparkSession(appName)

    import spark.implicits._

    val stream1 = spark
      .readStream
      .format(KAFKA)
      .option(BOOTSTRAP_SERVERS, KAFKA_BROKERS_LIST)
      .option(SUBSCRIBE, topicName)
      .option(STARTING_OFFSETS, startingOffsets)
      .load()

    val schema = StructType(
      Array(
        StructField("id", IntegerType, true),
        StructField("name", StringType, true),
        StructField("version", DoubleType, true),
        StructField("description", StringType, true)
      )
    )

    stream1.printSchema()

    val stream2 = stream1.selectExpr("topic", "partition", "offset", "CAST(key as STRING)", "CAST(value as STRING)")
    val stream3 = stream2.select(from_json(col("value"), schema).as("temp"), col("topic"), col("partition"), col("offset"))
    val stream4 = stream3.select("temp.*", "topic", "partition", "offset")

    val query = stream4.writeStream.foreachBatch( (ds: DataFrame, batchId: Long) => {

      ds.show()

      val df1 = ds
        .select("topic", "partition", "offset")
        .groupBy("topic", "partition")
        .agg(max("offset").as("offset"))

      val offsetsList = df1.as[KafkaOffset].collect().toList

      ds.select("id", "name", "version", "description")
        .withColumn("batch_id", lit(batchId))
        .write
        .format("jdbc")
        .option("url", "jdbc:mysql://localhost:3306/kafkadb")
        .option("driver", "com.mysql.jdbc.Driver")
        .option("dbtable", "technologies")
        .option("user", "kafkaspark")
        .option("password", "kafkaspark")
        .mode("append")
        .save()
      DBUtils.commitAllKafkaOffsets(offsetsList)
    }).start()

    query.awaitTermination()
  }

}
