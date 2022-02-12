package com.bigdata.spark.streaming.basic.file

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object ParseApacheLogFiles {
  private final val logPattern =  "^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(\\S+)\\s?(\\S+)?\\s?(\\S+)?\" (\\d{3}|-) (\\d+|-)\\s?\"?([^\"]*)\"?\\s?\"?([^\"]*)?\"?$".r()
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("ParseApacheLogFiles").setMaster("local[4]")
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    ///ssc.checkpoint("C:\\Users\\Ashok\\IdeaProjects\\StreamingExamples\\SparkStreamingExamples\\src\\main\\resources\\streaming-check-point-dir")

    val fileStream = ssc.textFileStream("C:\\Users\\Ashok\\IdeaProjects\\StreamingExamples\\SparkStreamingExamples\\src\\main\\resources\\file-source\\apache_logs")
    val stream2 = fileStream.map(logMessage => {
      val result = logPattern.findAllMatchIn(logMessage).toList
      val logLine = if(result.size == 1) {
        val res = result.head
        res.group(1) + ":::::" + res.group(2) + ":::::" + res.group(3) + ":::::" + res.group(4) + ":::::" + res.group(5) + ":::::" + res.group(6) + ":::::" +
          res.group(7) + ":::::" + res.group(8) + ":::::" + res.group(9) + ":::::" + res.group(10) + ":::::" + res.group(11)
      } else {
        ""
      }
      logLine
    })

    //println("Count :: " + stream2.count())

    fileStream.print()
    stream2.print()

    ssc.start()
    ssc.awaitTermination()

  }
}