package com.bigdata.spark.streaming.basic.socket

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object NetworkWordCountWithWindow {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("NetworkWordCount").setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val dStream1 = ssc.socketTextStream("localhost", 9999)
    val dStream2 = dStream1.flatMap(x => x.split(" "))
    val dStream3 = dStream2.map(x => (x, 1))
    val dStream4 = dStream3.reduceByKeyAndWindow((x: Int, y: Int) => (x + y), Seconds(30), Seconds(10))

    //val dStream4 = dStream3.reduceByKey((x, y) => (x + y))
    dStream4.print()

    ssc.start()
    ssc.awaitTermination()

  }
}