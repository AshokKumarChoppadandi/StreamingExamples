package com.bigdata.kafka.schemaregistry

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import org.apache.avro.Schema

object CRUD_Operations {
  def main(args: Array[String]): Unit = {

    val SCHEMA_REGISTRY_URL = "http://192.168.0.134:8081"
    val client = new CachedSchemaRegistryClient(SCHEMA_REGISTRY_URL, 100)

    // val schemaString = "{\"type\":\"record\",\"name\":\"myrecord1\",\"fields\":[{\"name\":\"f1\",\"type\":\"string\"}]}"
    val schemaString = "{\"type\":\"record\",\"name\":\"myrecord1\",\"fields\":[" +
      "{\"name\":\"dotomiId\",\"type\":\"int\"}," +
      "{\"name\":\"companyId\",\"type\":\"int\"}," +
      "{\"name\":\"serverId\",\"type\":\"int\"}," +
      "{\"name\":\"drmUserId\",\"type\":\"int\"}," +
      "{\"name\":\"formId\",\"type\":\"int\"}" +
      "]}"
    val schema = new Schema.Parser().parse(schemaString)

    val subject = "Adserver-value"
    client.register(subject, schema)

    println(s"Subject ${subject} created successfully...!!!")

  }
}
