package com.bigdata.spark.structured.streaming

import com.bigdata.spark.SparkSessionInitializer

object ReadKafkaTopicScala {
  def main(args: Array[String]): Unit = {
    val kafkaBrokers = "localhost:9092"
    val topicName = "test"
    val appName = "ReadKafkaTopicScala"

    val spark = SparkSessionInitializer.getSparkSession(appName)

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
    val query = df2.writeStream.outputMode("update").format("console").start()
    query.awaitTermination()


  }
}
