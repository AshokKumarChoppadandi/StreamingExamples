package com.bigdata.spark.streaming.utils

object ApplicationConstants {
  val DRIVER_NAME = "com.mysql.jdbc.Driver"
  val DB_URL = "jdbc:mysql://localhost:3306/kafkadb"
  val DB_USER_NAME = "kafkaspark"
  val DB_PASSWORD = "kafkaspark"

  val KAFKA_OFFSET_INSERT_QUERY = "UPDATE kafka_offsets_test SET OFFSET_VALUE = ? WHERE TOPIC_NAME = ? AND PARTITION_NUMBER = ? AND OFFSET_VALUE = ?"
  val KAFKA_OFFSET_INSERT_QUERY2 = "INSERT INTO kafka_offsets_test (TOPIC_NAME, PARTITION_NUMBER, OFFSET_VALUE) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE OFFSET_VALUE = ?"
  val KAFKA_OFFSET_READ_QUERY = "SELECT PARTITION_NUMBER, OFFSET_VALUE FROM kafka_offsets_test WHERE TOPIC_NAME = ?"
}
