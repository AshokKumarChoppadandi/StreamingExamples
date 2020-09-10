package com.bigdata.spark.listener

import org.apache.spark.scheduler.{SparkListener, SparkListenerApplicationEnd}
import org.apache.spark.sql.SparkSession

import scala.collection.JavaConversions._

class CustomSparkListener(spark: SparkSession) extends SparkListener {

  override def onApplicationEnd(applicationEnd: SparkListenerApplicationEnd): Unit = {
    val conf = spark.sparkContext.getConf
    val allProperties = conf.getAll
    println("Conf User :: " + conf.get("user.name", "DefaultUser"))
    println("Spark User :: " + spark.sparkContext.sparkUser)

    allProperties.foreach(println)
    val systemProperties = System.getenv()
    val keys = systemProperties.keySet()
    for (key <- keys) {
      println(s"${key}, " + systemProperties.getOrDefault(key, "No Value"))
    }
    println("Job has been completed successfully....!!!!!")
  }

}
