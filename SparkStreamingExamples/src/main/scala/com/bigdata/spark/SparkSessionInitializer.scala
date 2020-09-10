package com.bigdata.spark

import org.apache.spark.sql.SparkSession

object SparkSessionInitializer {
  def getSparkSession(appName: String): SparkSession = {
    val sparkSession = SparkSession.builder().appName(appName)/*.master("local[*]")*/.getOrCreate()
    sparkSession.sparkContext.setLogLevel("ERROR")
    sparkSession
  }
}
