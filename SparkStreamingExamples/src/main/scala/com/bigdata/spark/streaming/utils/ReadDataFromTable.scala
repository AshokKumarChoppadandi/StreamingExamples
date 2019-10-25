package com.bigdata.spark.streaming.utils

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

object ReadDataFromTable {
  def main(args: Array[String]): Unit = {
    val readQuery =
      """
        | SELECT offset FROM kafka_offsets WHERE TOPIC = ? AND `PARTITION` = ?
      """.stripMargin
    val conn: Connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kafkadb", "kafkaspark", "kafkaspark")
    val stmt: PreparedStatement = conn.prepareStatement(readQuery)

    stmt.setString(1, "test")
    stmt.setLong(2, 0L)

    val resultSet: ResultSet = stmt.executeQuery()

    val offset = if (resultSet.next()) {
      resultSet.getLong(1)
    } else {
      -1L
    }

    println("Topic : test, Partition: 0, Offset: " + offset)
  }
}
