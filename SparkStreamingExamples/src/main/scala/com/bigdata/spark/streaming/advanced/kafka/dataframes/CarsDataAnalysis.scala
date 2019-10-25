package com.bigdata.spark.streaming.advanced.kafka.dataframes

import com.bigdata.spark.streaming.utils.{DBUtils, OldCar}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.{Row, SaveMode, SparkSession}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.{Assign, Subscribe}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, KafkaUtils}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object CarsDataAnalysis {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("CarsDataAnalysis")//.set("es.batch.size.entries", "1")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    ssc.checkpoint("/home/ashok/IdeaProjects/SparkStreamingExamples/src/main/resources/cars-checkpoint-dir")
    ssc.sparkContext.setLogLevel("ERROR")

    val header = "maker,model,mileage,manufacture_year,engine_displacement,engine_power,body_type,color_slug,stk_year,transmission,door_count,seat_count,fuel_type,date_created,date_last_seen,price_eur"

    val topicName = Array("used_cars")

    val startingOffsets = DBUtils.readKafkaOffsetFromDB(topicName(0))
    startingOffsets.foreach(x => {
      val topicPartition = x._1
      val offset = x._2

      println("TopicName :: " + topicPartition.topic() + ", Partition :: " + topicPartition.partition() + ", Offset :: " + offset)
    })

    val kafkaParams = Map[String, Object] (
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "old-cars-group",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val dStream = if (startingOffsets.isEmpty) {
      println(s"No offsets Available in Database for topic :: ${topicName(0)}")
      KafkaUtils.createDirectStream[String, String](
        ssc,
        PreferConsistent,
        Subscribe[String, String](topicName, kafkaParams)
      )
    } else {
      println(s"Offsets are available in the database, hence reading topic: ${topicName(0)} using Offset Values")
      KafkaUtils.createDirectStream[String, String](
        ssc,
        PreferConsistent,
        Assign[String, String](startingOffsets.keys.toList, kafkaParams, startingOffsets)
      )
    }

    dStream.foreachRDD(rdd => {
      val offsets = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      rdd.map(x => x.value()).take(2).foreach(println)
      val spark = SparkSession.builder().config(rdd.sparkContext.getConf).getOrCreate()
      import spark.implicits._

      val carRecords = rdd.map(rec => rec.value()).filter(rec => !rec.contains(header))
      val rowRdd = carRecords.map(line => parseLine(line))

      val df = rowRdd.toDF()

      df.printSchema()
      df.show(10)

      println("Total Records :: " + df.count())
      df.write
        .format("org.elasticsearch.spark.sql")
        .option("es.nodes", "localhost")
        .option("es.port", "9200")
        .option("spark.es.nodes.client.only", "true")
        .mode(SaveMode.Append)
        .save("used_cars")

      DBUtils.commitAllKafkaOffsets(offsets)
    })

    ssc.start()
    ssc.awaitTermination()
  }

  def parseLine(line: String): OldCar = {
    val record = line.split(",")
    val mileage = convertToInt(record(3))
    val manufactureYear = convertToInt(record(4))
    val engineDisplacement = convertToInt(record(5))
    val enginePower = convertToInt(record(6))
    val priceEUR = convertToDouble(record(16))

    OldCar(record(0), record(1), record(2), mileage, manufactureYear, engineDisplacement, enginePower, record(7), record(8),
      record(9), record(10), record(11), record(12), record(13), record(14), record(15), priceEUR)

  }

  def convertToInt(str: String): Int = {
    if(str.equals("") || str.length == 0) 0 else str.toInt
  }

  def convertToDouble(str: String): Double = {
    if(str.equals("") || str.length == 0) 0 else str.toDouble
  }
}
