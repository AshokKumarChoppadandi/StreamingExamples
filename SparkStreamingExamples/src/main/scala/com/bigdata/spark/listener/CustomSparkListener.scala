package com.bigdata.spark.listener

import org.apache.spark.SparkConf
import org.apache.spark.scheduler.{SparkListener, SparkListenerApplicationEnd}

class CustomSparkListener(conf: SparkConf) extends SparkListener {

  override def onApplicationEnd(applicationEnd: SparkListenerApplicationEnd): Unit = {
    val allProperties = conf.getAll
    allProperties.foreach(println)
    println("Job has been completed successfully....!!!!!")
  }
}
