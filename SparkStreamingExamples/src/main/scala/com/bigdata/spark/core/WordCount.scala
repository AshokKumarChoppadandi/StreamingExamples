package com.bigdata.spark.core

import com.bigdata.spark.listener.CustomSparkListener
import org.apache.spark.sql.SparkSession

object WordCount {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("Spark WordCount").master("local").getOrCreate()

    val listener1 = new CustomSparkListener(spark.sparkContext.getConf)
    val listener2 = new CustomSparkListener(spark.sparkContext.getConf)

    spark.sparkContext.addSparkListener(listener1)
    spark.sparkContext.addSparkListener(listener2)

    val list = List(
      "Hi Hello World",
      "Hello Good Morning",
      "Hello Spark",
      "Kafka and Spark Integration",
      "Finally Hadoop"
    )

    val rdd1 = spark.sparkContext.parallelize(list, 2)
    val rdd2 = rdd1.flatMap(x => x.split("\\W+"))

    val rdd3 = rdd2.map(x => (x, 1))
    val rdd4 = rdd3.reduceByKey(_ + _)

    rdd4.collect.foreach(println)
  }
}