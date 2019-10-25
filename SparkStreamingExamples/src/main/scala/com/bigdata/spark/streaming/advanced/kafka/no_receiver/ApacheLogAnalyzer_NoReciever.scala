package com.bigdata.spark.streaming.advanced.kafka.no_receiver

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

import scala.collection.mutable


object ApacheLogAnalyzer_NoReciever {
  val KAFKA_OFFSET_INSERT_QUERY = "UPDATE kafka_offsets SET offset = ? WHERE TOPIC = ? AND `PARTITION` = ?"
  val KAFKA_OFFSET_READ_QUERY = "SELECT `PARTITION`, OFFSET FROM kafka_offsets where TOPIC = ?"
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("ApacheLogAnalyzer")
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    ssc.checkpoint("/home/ashok/IdeaProjects/SparkStreamingExamples/src/main/resources/checkpoint-dir")
    ssc.sparkContext.setLogLevel("ERROR")

    val topicName = "test"
    var conn: Connection = null;
    var stmt: PreparedStatement = null;
    var stmt2: PreparedStatement = null;
    var startingOffSets = Map[TopicPartition, Long]()
    try{
      //conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/kafkadb", "kafkaspark", "kafkaspark");
      //conn.setAutoCommit(false)
      //stmt = conn.prepareStatement(KAFKA_OFFSET_INSERT_QUERY)
      /*stmt2 = conn.prepareStatement(KAFKA_OFFSET_READ_QUERY)
      stmt2.setString(1, topicName)
      val resultSet = stmt2.executeQuery()
      while (resultSet.next()) {
        val partitionValue = resultSet.getInt(1)
        val offset = resultSet.getLong(2)
        startingOffSets += (new TopicPartition(topicName, partitionValue) -> offset)
      }*/
    }
    /*startingOffSets.foreach(x => {
      val topicPartition = x._1
      val offset = x._2

      println("TopicName :: " + topicPartition.topic() + ", Partition :: " + topicPartition.partition() + ", Offset :: " + offset)
    })*/

    val kafkaParams = Map[String, Object] (
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "test-group",
      /*"auto.offset.reset" -> "latest",*/
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    //apache-logs

    val startingOffSet = Map[TopicPartition, Long] (new TopicPartition("test", 0) -> 10L)
    val topics = Array("test")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams, startingOffSet)
    )

    /*val stream2 = stream.map(record => (record.key(), record.value(), record.topic(), record.partition(), record.offset()))
    stream2.print()*/

    stream.foreachRDD(rdd => {
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      val result = rdd.map(record => (record.topic(), record.partition(), record.offset(), record.key(), record.value()))
      result.collect().foreach(println)

      offsetRanges.foreach(offSetRange => {
        println(offSetRange.topic, offSetRange.partition, offSetRange.untilOffset)
        val topic = offSetRange.topic
        val partition = offSetRange.partition
        val offset = offSetRange.untilOffset

        println(
          s"""
            | TOPIC : ${topic}
            | PARTITION : ${partition}
            | OFFSET : ${offset}
          """.stripMargin)
        /*stmt.setString(1, topic)
        stmt.setLong(2, partition)
        stmt.setLong(3, offset)

        val affectedRecords = stmt.executeUpdate()
        if(affectedRecords == 1) {
          println("Inserted Offsets")
        }*/
        /*
        if(affectedRecords == 1) {
          conn.commit()
          if (conn != null) conn.close()
        } else {
          conn.rollback()
          if (conn != null) conn.close()
          throw new RuntimeException("Unable to update Kafka Offsets")
        }*/
      })
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
