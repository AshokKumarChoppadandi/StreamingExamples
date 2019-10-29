package com.bigdata.spark.streaming.utils

import java.sql.{Connection, DriverManager, SQLException}

import com.bigdata.spark.models.{KafkaOffset, PartitionOffset}
import com.bigdata.spark.streaming.utils.ApplicationConstants._
import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.kafka010.OffsetRange

import scala.collection.mutable.ListBuffer

object DBUtils {
  def getJdbcConnection(): Connection = {
    try {
      Class.forName(DRIVER_NAME)
      val connection = DriverManager.getConnection(DB_URL, DB_USER_NAME, DB_PASSWORD)
      connection
    } catch {
      case ex: SQLException => {
        println("Exception while connecting to Database")
        ex.printStackTrace()
        throw ex
      }
      case e: Exception => {
        e.printStackTrace()
        throw e
      }
    }
  }

  def readKafkaOffsetFromDB(topicName: String): scala.collection.mutable.Map[TopicPartition, Long] = {
    val topicOffsets =  scala.collection.mutable.Map[TopicPartition, Long]()
    val dbConn = getJdbcConnection()
    val statement = dbConn.prepareStatement(KAFKA_OFFSET_READ_QUERY)
    statement.setString(1, topicName)

    val resultSet = statement.executeQuery()

    while (resultSet.next()) {
      val topicPartition = new TopicPartition(topicName, resultSet.getInt(1))
      topicOffsets(topicPartition) = resultSet.getLong(2)
    }

    topicOffsets
  }

  def readKafkaOffsetFromDBAsJsonString(topicName: String): String = {
    val topicOffsets = ListBuffer[String]()
    val dbConn = getJdbcConnection()
    val statement = dbConn.prepareStatement(KAFKA_OFFSET_READ_QUERY)
    statement.setString(1, topicName)

    val resultSet = statement.executeQuery()

    while (resultSet.next()) {
      topicOffsets +=
        s"""
          | "${resultSet.getInt(1).toString}":${resultSet.getLong(2) + 1}
        """.stripMargin
    }

    val returnString = "{\"" + topicName + "\":{" + topicOffsets.toList.map(x => x.trim).mkString(",") + "}}"
    returnString
  }

  def commitKafkaOffsets(topicName: String, partitionNumber: Int, offsetFrom: Long, offsetTo: Long): Unit = {
    var connection: Connection = null
    try {
      connection = getJdbcConnection()
      val statement = connection.prepareStatement(KAFKA_OFFSET_INSERT_QUERY)
      statement.setLong(1, offsetTo)
      statement.setString(2, topicName)
      statement.setInt(3, partitionNumber)
      statement.setLong(4, offsetFrom)

      statement.execute()
    } catch {
      case ex: SQLException => {
        println("Error while updating the kafka offsets on to the database...!!!")
        ex.printStackTrace()
        throw ex
      }
      case e: Exception => {
        println("Error Occurred...!!!")
        e.printStackTrace()
        throw e
      }
    } finally {
      closeDatabaseConnection(connection)
    }
  }

  def commitAllKafkaOffsets(offsetRanges: Array[OffsetRange]): Unit = {
    var connection: Connection = null
    try {
      connection = getJdbcConnection()
      connection.setAutoCommit(false)
      val statement = connection.prepareStatement(KAFKA_OFFSET_INSERT_QUERY)

      offsetRanges.foreach(offsetRange => {
        statement.setLong(1, offsetRange.untilOffset)
        statement.setString(2, offsetRange.topic)
        statement.setInt(3, offsetRange.partition)
        statement.setLong(4, offsetRange.fromOffset)
        statement.addBatch()
      })
      statement.executeBatch()
      connection.commit()
    } catch {
      case ex: SQLException => {
        println("Error while updating the kafka offsets on to the database...!!!")
        ex.printStackTrace()
        throw ex
      }
      case e: Exception => {
        println("Error Occurred...!!!")
        e.printStackTrace()
        throw e
      }
    } finally {
      closeDatabaseConnection(connection)
    }
  }

  def commitAllKafkaOffsets(offsetsList: List[KafkaOffset]): Unit = {
    var connection: Connection = null
    try {
      connection = getJdbcConnection()
      connection.setAutoCommit(false)
      val statement = connection.prepareStatement(KAFKA_OFFSET_INSERT_QUERY2)
      offsetsList.foreach(offsetRange => {
        statement.setString(1, offsetRange.topic)
        statement.setInt(2, offsetRange.partition)
        statement.setLong(3, offsetRange.offset)
        statement.setLong(4, offsetRange.offset)
        statement.addBatch()
      })
      statement.executeBatch()
      connection.commit()
      println("_" * 50 + "\n" + "Offsets committed successfully...!!!\n" + "_" * 50)
    } catch {
      case ex: SQLException => {
        println("Error while updating the kafka offsets on to the database...!!!")
        ex.printStackTrace()
        throw ex
      }
      case e: Exception => {
        println("Error Occurred...!!!")
        e.printStackTrace()
        throw e
      }
    } finally {
      closeDatabaseConnection(connection)
    }
  }



  def closeDatabaseConnection(conn: Connection): Unit = {
    try {
      if (conn != null) conn.close()
    } catch {
      case ex: Exception => {
        println("Error while closing the database connection...!!!")
        ex.printStackTrace()
        throw ex
      }
    }
  }
}
