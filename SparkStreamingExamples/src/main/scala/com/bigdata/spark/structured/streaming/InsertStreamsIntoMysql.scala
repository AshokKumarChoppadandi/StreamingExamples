package com.bigdata.spark.structured.streaming

import com.bigdata.spark.SparkSessionInitializer
import com.bigdata.spark.structured.streaming.ScalaUtils._
import org.apache.spark.sql.functions._
import org.apache.spark.sql._
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructField, StructType}

object InsertStreamsIntoMysql {
  def main(args: Array[String]): Unit = {

    val appName = "Insert Structured Streams Into Mysql Database Table"
    val topicName = "test-with-schema"
    val spark = SparkSessionInitializer.getSparkSession(appName)

    val stream1 = spark
      .readStream
      .format(KAFKA)
      .option(BOOTSTRAP_SERVERS, KAFKA_BROKERS_LIST)
      .option(SUBSCRIBE, topicName)
      .option(STARTING_OFFSETS, EARLIEST)
      .load()

    val schema = StructType(
      Array(
        StructField("id", IntegerType, true),
        StructField("name", StringType, true),
        StructField("version", DoubleType, true),
        StructField("description", StringType, true)
      )
    )

    val stream2 = stream1.selectExpr("CAST(key as STRING)", "CAST(value as STRING)")
    val stream3 = stream2.select(from_json(col("value"), schema).as("temp"))
    val stream4 = stream3.select("temp.*")

    stream4.printSchema()

    val query = stream4.writeStream.foreachBatch( (df: DataFrame, batchId: Long) => {
      df.show()
      df
        .withColumn("batch_id", lit(batchId))
        .write
        .format("jdbc")
        .option("url", "jdbc:mysql://localhost:3306/kafkadb")
        .option("driver", "com.mysql.jdbc.Driver")
        .option("dbtable", "technologies")
        .option("user", "kafkaspark")
        .option("password", "kafkaspark")
        .mode("append").save()

      // To insert into Hive Table
      //df.write.insertInto("dbName.tableName")

    }).start()
    query.awaitTermination()
  }
}
