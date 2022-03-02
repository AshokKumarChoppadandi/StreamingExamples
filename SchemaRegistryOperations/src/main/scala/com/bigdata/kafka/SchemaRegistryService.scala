package com.bigdata.kafka

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import io.confluent.kafka.serializers.{KafkaAvroDeserializer, KafkaAvroDeserializerConfig, KafkaAvroSerializer}
import org.apache.avro.generic.GenericRecord
import org.apache.spark.sql.Row

import java.util

class SchemaRegistryService(schemaRegistryUrl: String, cacheCapacity: Int) extends Serializable {
  val getSchemaRegistryClient = {
    new CachedSchemaRegistryClient(schemaRegistryUrl, cacheCapacity)
  }

  val getConfigs = {
    val map = new util.HashMap[String, String]()
    map.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "false")
    map.put("schema.registry.url", schemaRegistryUrl)
    map
  }

  val avroDeserializer = new KafkaAvroDeserializer(getSchemaRegistryClient, getConfigs)
  val avroSerializer = new KafkaAvroSerializer(getSchemaRegistryClient, getConfigs)

  def deserialize(topic: String, record: Array[Byte]) = avroDeserializer.deserialize(topic, record)

  def convertAvroToRow(topic: String, record: Array[Byte]): Row = {
    val genericRecord = avroDeserializer.deserialize(topic, record).asInstanceOf[GenericRecord]
    null
  }

}
