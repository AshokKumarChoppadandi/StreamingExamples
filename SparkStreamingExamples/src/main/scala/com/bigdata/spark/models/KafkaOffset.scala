package com.bigdata.spark.models

case class KafkaOffset(topic: String, partition: Int, offset: Long)
