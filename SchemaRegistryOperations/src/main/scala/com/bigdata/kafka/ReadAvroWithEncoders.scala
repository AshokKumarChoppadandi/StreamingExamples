package com.bigdata.kafka

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.avro.Schema
import org.apache.spark.sql.avro.SchemaConverters
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.StructType

import scala.collection.JavaConverters.mapAsJavaMapConverter

case class Employee(eId: Int, eName: String, eSalary: Int, eDept: String, eAge: Int)

object ReadAvroWithEncoders {
  def main(args: Array[String]): Unit = {

    val SCHEMA_REGISTRY_URL = "http://localhost:8081"
    val TOPIC_NAME = "employee"

    val sparkSession = SparkSession
      .builder()
      .appName("ReadAvroWithEncoders")
      .master("local")
      .getOrCreate()

    import sparkSession.implicits._
    val client = new CachedSchemaRegistryClient(SCHEMA_REGISTRY_URL, 100)
    val schemaString = client.getLatestSchemaMetadata(TOPIC_NAME + "-value").getSchema

    val parser = new Schema.Parser()
    val avroSchema = parser.parse(schemaString)

    val props = Map("schema.registry.url" -> "http://192.168.0.134:8081")
    var valueDeserializer: KafkaAvroDeserializer = null

    val ds1 = sparkSession
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "192.168.0.211:9092,192.168.0.212:9092,192.168.0.213:9092")
      .option("startingOffsets", "earliest")
      .option("subscribe", TOPIC_NAME)
      .load()
      .select("value")
      .as[Array[Byte]]

    val ds2 = ds1.map(row => {
      if (valueDeserializer == null) {
        valueDeserializer = new KafkaAvroDeserializer()
        valueDeserializer.configure(props.asJava, false)
      }

      valueDeserializer.deserialize(TOPIC_NAME, row, avroSchema).toString
    })

    val structType = SchemaConverters.toSqlType(avroSchema).dataType.asInstanceOf[StructType]

    val ds3 = ds2
      .withColumn("json_fields", from_json(col("value"), structType))
      .select(col("json_fields.*"))
      .as[Employee]

    ds3.printSchema()

    val ds4 = ds3
      .writeStream
      .outputMode("Append")
      .format("console")
      .option("truncate", value = false)
      .trigger(Trigger.Once())
      .start()
    ds4.awaitTermination()
  }
}
