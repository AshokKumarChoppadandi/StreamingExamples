package com.bigdata.spark.structured.streaming

import com.bigdata.spark.SparkSessionInitializer
import com.bigdata.spark.listener.{CustomSparkListener, CustomStreamingQueryListener}
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructField, StructType}

object CsvFileStreaming extends App {
  val spark = SparkSessionInitializer.getSparkSession("Csv File Streaming")
  spark.streams.addListener(new CustomStreamingQueryListener)
  spark.sparkContext.addSparkListener(new CustomSparkListener(spark.sparkContext.getConf))
  val schema = StructType(
    Array(
      StructField("id", IntegerType, true),
      StructField("name", StringType, true),
      StructField("salary", DoubleType, true)
    )
  )

  val df = spark.readStream.option("sep", ",").schema(schema).csv("/home/ashok/IdeaProjects/StreamingExamples/SparkStreamingExamples/src/main/resources/CsvStreaming")
  df.isStreaming

  val query = df.writeStream.outputMode("update").format("console").start()

  query.awaitTermination()

}
