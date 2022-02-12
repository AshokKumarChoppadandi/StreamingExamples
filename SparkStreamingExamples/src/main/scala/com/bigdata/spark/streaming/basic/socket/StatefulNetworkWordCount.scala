package com.bigdata.spark.streaming.basic.socket

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object StatefulNetworkWordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("NetworkWordCount").setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    ssc.checkpoint("/home/ashok/IdeaProjects/SparkStreamingExamples/src/main/resources/checkpoint-dir")

    val dStream1 = ssc.socketTextStream("localhost", 9999)
    val dStream2 = dStream1.flatMap(x => x.split(" "))
    val dStream3 = dStream2.map(x => (x, 1))
    val dStream4 = dStream3.reduceByKey(_ + _)
    val dStream5 = dStream4.updateStateByKey[Int](updateFunction _)
    dStream5.print()

    ssc.start()
    ssc.awaitTermination()
  }

  def updateFunction(newValues: Seq[Int], runningCount: Option[Int]): Option[Int] = {
    val newSum = runningCount.getOrElse(0) + newValues.sum
    Some(newSum)
  }
}
