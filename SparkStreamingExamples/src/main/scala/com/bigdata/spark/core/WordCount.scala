package com.bigdata.spark.core

import com.bigdata.spark.SparkSessionInitializer
import com.bigdata.spark.listener.CustomSparkListener

object WordCount {
  def main(args: Array[String]): Unit = {

    val spark = SparkSessionInitializer.getSparkSession("WordCount")
    spark.sparkContext.addSparkListener(new CustomSparkListener(spark))

    import spark.implicits._

    val list = List(
      "Test Test Test1 Test2 Test3",
      "Test Test Test1 Test2 Test3",
      "Test Test Test1 Test2 Test3",
      "Test Test Test1 Test2 Test3",
      "Test Test Test1 Test2 Test3"
    )

    val sentences = list.toDS
    val words = sentences.flatMap(sentence => sentence.split("\\W+"))
    val groupedWords = words.groupBy("value")

    val wordCount = groupedWords.count()
    wordCount.show()

    Thread.sleep(2 * 60 * 1000)
  }
}
