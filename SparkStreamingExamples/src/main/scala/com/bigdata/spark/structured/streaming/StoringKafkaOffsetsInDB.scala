package com.bigdata.spark.structured.streaming

import com.bigdata.spark.SparkSessionInitializer
import com.bigdata.spark.models.{KafkaOffset, PartitionOffset, Technology}
import com.bigdata.spark.streaming.utils.DBUtils
import com.bigdata.spark.structured.streaming.ScalaUtils.{BOOTSTRAP_SERVERS, EARLIEST, KAFKA, KAFKA_BROKERS_LIST, STARTING_OFFSETS, SUBSCRIBE}
import org.apache.spark.sql.{DataFrame, Dataset}
import org.apache.spark.sql.functions.{col, from_json, lit, max}
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructField, StructType}

object StoringKafkaOffsetsInDB {
  def main(args: Array[String]): Unit = {
    val appName = "Storing Kafka Offsets in Database"
    val topicName = "test-with-schema"
    val spark = SparkSessionInitializer.getSparkSession(appName)
    import spark.implicits._

    val stream1 = spark
      .readStream
      .format(KAFKA)
      .option(BOOTSTRAP_SERVERS, KAFKA_BROKERS_LIST)
      .option(SUBSCRIBE, topicName)
      .option(STARTING_OFFSETS, EARLIEST)
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

    /*val offsets = stream1
      .selectExpr("topic", "partition", "offset")
      .groupBy("topic", "partition")
      .agg(max("offset").as("offset"))
      .as[KafkaOffset]*/
    /*val stream2 = stream1.selectExpr("CAST(key as STRING)", "CAST(value as STRING)")
    val stream3 = stream2.select(from_json(col("value"), schema).as("temp"))
    val stream4 = stream3.select("temp.*").as[Technology]

    stream4.printSchema()*/
    //offsets.printSchema()

    /*val query = stream4.writeStream.foreachBatch( (ds: Dataset[Technology], batchId: Long) => {
      ds.show()

      ds
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
    }).start()*/

    /*val query2 = offsets.writeStream.foreachBatch( (ds: Dataset[KafkaOffset], batchId: Long) => {
      ds.show()
      //val offsetsList = ds.collect().toList
      //DBUtils.commitAllKafkaOffsets(offsetsList)
    }).start()*/

    //query.awaitTermination()
    //query2.awaitTermination()

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
