package com.bigdata.kafka.schemaregistry

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import org.apache.avro.Schema

object CRUD_Operations {
  def main(args: Array[String]): Unit = {

    val SCHEMA_REGISTRY_URL = "http://192.168.0.134:8081"
    val client = new CachedSchemaRegistryClient(SCHEMA_REGISTRY_URL, 100)

    val schemaString = "{\"type\":\"record\",\"name\":\"myrecord1\",\"fields\":[{\"name\":\"f1\",\"type\":\"string\"}]}"
    val schema = new Schema.Parser().parse(schemaString)

    val subject = "Test-value"
    client.register(subject, schema)

    println(s"Subject ${subject} created successfully...!!!")

  }
}
