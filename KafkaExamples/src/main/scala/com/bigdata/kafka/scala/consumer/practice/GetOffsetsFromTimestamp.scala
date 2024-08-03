package com.bigdata.kafka.scala.consumer.practice

import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer, OffsetAndTimestamp}
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer

import java.util.Properties
import scala.collection.JavaConverters._
import scala.collection.mutable

object GetOffsetsFromTimestamp {
  def main(args: Array[String]): Unit = {
    val properties = new Properties();

    val bootstrapServers = "192.168.0.113:9092"
    val topicName = "logs-test"
    val epochTimestamp = "1722384000000".toLong.asInstanceOf[java.lang.Long]

    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "test-group1")

    val consumer = new KafkaConsumer[String, String](properties)
    val partitionsInfo = consumer.partitionsFor(topicName)

    val topicPartitionWithTimestamp = partitionsInfo
      .asScala
      .toList
      .map(x => new TopicPartition(x.topic(), x.partition()) -> epochTimestamp)
      .toMap
      .asJava

    val topicPartitionOffsetsAndTimestamp = consumer.offsetsForTimes(topicPartitionWithTimestamp).asScala

    val topicPartitionOffsetsAndTimestampSorted = sortKafkaTopicPartitions(topicPartitionOffsetsAndTimestamp)
    println("\tTopic\t\t|\t\tPartition\t\t|\t\tOffset\t")

    topicPartitionOffsetsAndTimestampSorted.foreach {
      case (topicPartition: TopicPartition, offsetAndTimestamp: OffsetAndTimestamp) =>
        println(s"\t${topicPartition.topic()}\t\t|\t\t${topicPartition.partition()}\t\t|\t\t${offsetAndTimestamp.offset()}")
    }
  }

  private def sortKafkaTopicPartitions(topicPartitionOffsetsAndTimestamp: mutable.Map[TopicPartition, OffsetAndTimestamp]): Seq[(TopicPartition, OffsetAndTimestamp)] = {
    val res = topicPartitionOffsetsAndTimestamp.toSeq.sortBy(x => x._1.partition())
    println(res)
    res
  }
}
