package com.bigdata.kafka

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import io.confluent.kafka.serializers.{KafkaAvroDeserializer, KafkaAvroDeserializerConfig, KafkaAvroSerializer}
import org.apache.avro.Schema
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.avro.SchemaConverters
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.functions.{col, lit}
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.StructType

import java.util.Properties
import scala.collection.JavaConverters._

// case class Employee(eId: Int, eName: String, eSalary: Int, eDept: String, eAge: Int)

object ReadAvro {
  def main(args: Array[String]): Unit = {

    val SCHEMA_REGISTRY_URL = "http://localhost:8081"
    val TOPIC_NAME = "Test"
    val spark = SparkSession.builder().appName("ReadAvro").master("local").getOrCreate()

    import spark.implicits._

    /*val client = new CachedSchemaRegistryClient(SCHEMA_REGISTRY_URL, 100)

    val map = new util.HashMap[String, String]()
    map.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "false")
    map.put("schema.registry.url", SCHEMA_REGISTRY_URL)

    val avroDeserializer = new KafkaAvroDeserializer(client, map)
    val avroSerializer = new KafkaAvroSerializer(client, map)

    // val schemaRegistryService = new SchemaRegistry*/

    /*val schemaRegistryService = new SchemaRegistryService(SCHEMA_REGISTRY_URL, 100)
    val schema =  schemaRegistryService.getSchemaRegistryClient.getLatestSchemaMetadata(TOPIC_NAME + "-value")

    val sqlSchema = SchemaConverters
      .toSqlType(new Schema.Parser().parse(schema.getSchema))
      .dataType
      .asInstanceOf[StructType]*/

    val client = new CachedSchemaRegistryClient(SCHEMA_REGISTRY_URL, 100)
    val schemaString = client.getLatestSchemaMetadata(TOPIC_NAME + "-value").getSchema
    val parser = new Schema.Parser()
    val avroSchema = parser.parse(schemaString)

    val props = Map("schema.registry.url" -> "http://localhost:8081")

    var valueDeserializer: KafkaAvroDeserializer = null

    val ds1 = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "192.168.0.211:9092,192.168.0.212:9092,192.168.0.213:9092")
      .option("subscribe", "Test")
      .option("startingOffsets", """{"Test":{"0":5}}""")
      .load()
      .select("value")
      .as[Array[Byte]]
      //.map(record => schemaRegistryService.deserialize(TOPIC_NAME, record))(RowEncoder(sqlSchema))*/

    val ds2 = ds1.map(row => {
      if (valueDeserializer == null) {
        valueDeserializer = new KafkaAvroDeserializer()
        valueDeserializer.configure(props.asJava, false)
      }

      valueDeserializer.deserialize(TOPIC_NAME, row, avroSchema).toString
    })

/*    val ds3 = ds2
      .writeStream
      .outputMode("Append")
      .format("console")
      .option("truncate", value = false)
      .trigger(Trigger.Once())
      .start()*/

    val connectionProperties = new Properties()
    connectionProperties.put("user", "bigdata")
    connectionProperties.put("password", "bigdata")
    // connectionProperties.put("stringtype", "unspecified")
    connectionProperties.put("driver", "org.postgresql.Driver")


    val ds4 = ds2
      .writeStream
      .trigger(Trigger.Once())
      .foreachBatch((data, batchId) => {
        data.printSchema()
        data.withColumn("test_col", lit("tmp")).show()
        data
          .withColumn("test_col", lit("tmp"))
          .select(col("test_col"), col("value").as("json_data"))
          .write
          .mode(SaveMode.Append)
          // .jdbc("jdbc:postgresql://192.168.0.144:5432/bigdatadb", "test", connectionProperties)
          .format("jdbc")
          .option("driver", "org.postgresql.Driver")
          .option("url", "jdbc:postgresql://192.168.0.144:5432/bigdatadb")
          .option("dbtable", "test")
          .option("user", "bigdata")
          .option("password", "bigdata")
          .option("stringtype", "unspecified")
          .save()

      }).start()
    // ds3.awaitTermination()
    ds4.awaitTermination()
  }
}
