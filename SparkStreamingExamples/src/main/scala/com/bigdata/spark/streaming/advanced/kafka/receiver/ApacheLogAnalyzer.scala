package com.bigdata.spark.streaming.advanced.kafka.receiver

import com.bigdata.spark.streaming.utils.{ApacheAccessLog, CommonUtils}
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object ApacheLogAnalyzer {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("ApacheLogAnalyzer")
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    ssc.checkpoint("/home/ashok/IdeaProjects/SparkStreamingExamples/src/main/resources/checkpoint-dir")
    ssc.sparkContext.setLogLevel("ERROR")

    val mapArgs = Map[String, Int]("apache-logs" -> 1)

    val logsStream = KafkaUtils.createStream(ssc, "localhost:2181", "apache-log-group", mapArgs, StorageLevel.MEMORY_AND_DISK_SER_2)
    val parsedLogs = logsStream.map(x => {
      val logMessage = x._2
      val result = CommonUtils.logPattern.findAllMatchIn(logMessage).toList
      if(result.size == 1) {
        val res = result(0)
        ApacheAccessLog(res.group(1), res.group(2), res.group(3), res.group(4), res.group(5), res.group(6), res.group(7), res.group(8).toInt, res.group(9).toLong, res.group(10), res.group(11))
      } else
        null
    })

    val filteredApacheLogs = parsedLogs.filter(logMessage => logMessage != null)
    val responseCodeLogs = filteredApacheLogs.map(apacheLog => (apacheLog.responseCode, 1))
    val responseCountsIn10Seconds = responseCodeLogs.reduceByKeyAndWindow((x: Int, y: Int) => (x + y), Seconds(10), Seconds(5))
    val statefulResponseCounts = responseCountsIn10Seconds.updateStateByKey[Int](updateFunction _)

    statefulResponseCounts.print()

    ssc.start()
    ssc.awaitTermination()

  }

  def updateFunction(newRecords: Seq[Int], fullCount: Option[Int]): Option[Int] = {
    val newSum = fullCount.getOrElse(0) + newRecords.sum
    Some(newSum)
  }
}
