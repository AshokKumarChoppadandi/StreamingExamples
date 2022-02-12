package com.bigdata.elasticsearch.spark.sql

import org.apache.spark.sql.SparkSession

object ReadFromES {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("Read from Elasticsearch").master("local").getOrCreate()

    val df = spark.read.format("org.elasticsearch.spark.sql").option("es.nodes", "localhost").option("es.port", 9200).load("cars2/old_cars")

    df.show(5)
  }
}
