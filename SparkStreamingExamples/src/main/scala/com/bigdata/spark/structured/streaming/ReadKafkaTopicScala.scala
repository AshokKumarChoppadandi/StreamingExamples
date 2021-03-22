package com.bigdata.spark.structured.streaming

import com.bigdata.spark.SparkSessionInitializer
import com.bigdata.spark.listener.CustomStreamingQueryListener
import org.apache.spark.sql.streaming.Trigger

object ReadKafkaTopicScala {
  def main(args: Array[String]): Unit = {
    val kafkaBrokers = "localhost:9092"
    val topicName = "test"
    val appName = "ReadKafkaTopicScala"

    val spark = SparkSessionInitializer.getSparkSession(appName)

    val listener = new CustomStreamingQueryListener()
    spark.streams.addListener(listener)

    val messages = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBrokers)
      .option("subscribe", topicName)
      .option("startingOffsets", "earliest")
      .load()

    println("_" * 50)
    println("IS STREAMING :: " + messages.isStreaming)
    println("_" * 50)
    //val data = messages.selectExpr("CAST(value as STRING)", "CAST(timestamp as TIMESTAMP)")

    val df2 = messages.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
    val query = df2.writeStream.outputMode("update").format("console").trigger(Trigger.Once()).start()
    query.awaitTermination()

    println("Final Input Records :: " + listener.returnFinalCount())

  }
}
