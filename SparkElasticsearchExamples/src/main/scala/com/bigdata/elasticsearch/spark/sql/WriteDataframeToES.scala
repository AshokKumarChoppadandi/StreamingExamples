package com.bigdata.elasticsearch.spark.sql

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.elasticsearch.spark.sql._
import org.apache.spark.sql.functions._

object WriteDataframeToES {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("Write Dataframe to Elasticsearch")
      .master("local")
      /*.config("es.nodes", "localhost")
      .config("es.port", "9200")
      .config("spark.es.nodes.client.only", "true")*/
      .getOrCreate()
    val df = spark.read.option("header", true).csv("/home/ashok/Desktop/Data/Cars_Test.csv")

    val df1 = df.withColumn("_id", monotonically_increasing_id())
    df1.show()

    df1
      .write
      .format("org.elasticsearch.spark.sql")
      .option("es.nodes", "localhost")
      .option("es.port", "9200")
      .option("spark.es.nodes.client.only", "true")
      .mode(SaveMode.Append)
      .save("cars_id_test/oldcars")
  }
}
