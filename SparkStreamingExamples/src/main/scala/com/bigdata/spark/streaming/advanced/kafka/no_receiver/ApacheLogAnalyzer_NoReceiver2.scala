package com.bigdata.spark.streaming.advanced.kafka.no_receiver

import java.sql.{Connection, PreparedStatement}

import com.bigdata.spark.streaming.utils.DBUtils
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Assign
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, KafkaUtils}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

object ApacheLogAnalyzer_NoReceiver2 {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("ApacheLogAnalyzer")
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    ssc.checkpoint("/home/ashok/IdeaProjects/SparkStreamingExamples/src/main/resources/checkpoint-dir")
    ssc.sparkContext.setLogLevel("ERROR")

    val topicName = "new-test"

    val startingOffsets = DBUtils.readKafkaOffsetFromDB(topicName)
    startingOffsets.foreach(x => {
      val topicPartition = x._1
      val offset = x._2

      println("TopicName :: " + topicPartition.topic() + ", Partition :: " + topicPartition.partition() + ", Offset :: " + offset)
    })

    val kafkaParams = Map[String, Object] (
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "test-group",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Assign[String, String](startingOffsets.keys.toList, kafkaParams, startingOffsets)
    )

    stream.foreachRDD(rdd => {
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      val result = rdd.map(record => (record.topic(), record.partition(), record.offset(), record.key(), record.value()))
      result.collect().foreach(println)

      /*offsetRanges.foreach(offSetRange => {
        println(offSetRange.topic, offSetRange.partition, offSetRange.untilOffset)
        val topic = offSetRange.topic
        val partition = offSetRange.partition
        val fromOffset = offSetRange.fromOffset
        val toOffset = offSetRange.untilOffset

        println(
          s"""
             | TOPIC : ${topic}
             | PARTITION : ${partition}
             | FROM OFFSET : ${fromOffset}
             | TO OFFSET : ${toOffset}
          """.stripMargin)
        DBUtils.commitKafkaOffsets(topic, partition, fromOffset, toOffset)
      })*/

      DBUtils.commitAllKafkaOffsets(offsetRanges)
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
