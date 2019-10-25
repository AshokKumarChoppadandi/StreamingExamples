package com.bigdata.spark.streaming.advanced.kafka.receiver

import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object KafkaWordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("KafkaWordCount")
    val ssc = new StreamingContext(sparkConf, Seconds(10))

    val mapArgs = Map[String, Int]("test" -> 1)

    val dStream1 = KafkaUtils.createStream(ssc, "localhost:2181", "group1", mapArgs, StorageLevel.MEMORY_AND_DISK_SER_2)

    val dStream2 = dStream1.transform(x => {
      val messages = x.map(msg => msg._2)
      val words = messages.flatMap(msg => msg.split(" "))
      val pairs = words.map(word => (word, 1))
      pairs.reduceByKey(_ + _)
    })

    dStream2.print()

    ssc.start()
    ssc.awaitTermination()

  }
}
