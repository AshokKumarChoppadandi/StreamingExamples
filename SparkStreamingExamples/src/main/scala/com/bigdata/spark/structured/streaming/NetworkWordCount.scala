package com.bigdata.spark.structured.streaming

import org.apache.spark.sql.SparkSession

object NetworkWordCount extends App {
  val spark = SparkSession
    .builder
    .appName("StructuredNetworkWordCount")
    .master("local[*]")
    .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")
  import spark.implicits._

  val lines = spark.readStream.format("socket").option("host", "localhost").option("port", 9999).load()

  //val words = lines.as[String].flatMap(_.split(" "))
  //val wordCount = words.groupBy("value").count()
  //val query = wordCount.writeStream.outputMode("update").format("console").start()
  //query.awaitTermination()

  lines.writeStream.outputMode("update").format("console").start().awaitTermination()
}
