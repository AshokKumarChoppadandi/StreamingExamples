package com.bigdata.kafka.scala.admin.groups

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer, OffsetAndTimestamp}
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import ujson.Value

import java.net.{HttpURLConnection, URL}
import HttpURLConnection._
import java.io.OutputStreamWriter
import java.util.Properties
import java.{lang, util}
import scala.io.Source.fromInputStream
import scala.collection.JavaConverters._
import scala.collection.mutable

object ConsumerGroupResetOffsetsWithTimestamp {
  def main(args: Array[String]): Unit = {
    val config = new Conf(args)
    println(config.summary)

    val propertiesFile = config.propertiesFile.toOption.get
    val bootstrapServerUrl = config.bootstrapServerUrl.toOption.get
    val connectServerUrl = config.connectServerUrl.toOption.get
    val epochTimestamp = config.epochTimestamp.toOption
    val connectorConfig = ujson.read(os.read(os.Path(propertiesFile)))

    val connectorName = connectorConfig("name").str
    val topicName = connectorConfig("config")("topics").str
    val connectorConsumerGroup = s"connect-${connectorName}"
    val connectServerBaseUrl = getValidConnectServerUrl(connectServerUrl)
    println(s"Connect Server Base URL - ${connectServerBaseUrl}")

    val connectServerVersion = getConnectServerVersion(connectServerBaseUrl)
    println(connectServerVersion)

    val action = config.action.toOption.get
    val validAction = if (connectServerVersion.startsWith("6.") && List("resume", "stop").contains(action)) {
      println(s"Action `${action}` is not supported in Kafka Connect v${connectServerVersion}")
      null
    } else {
      action
    }

    val properties = new Properties()

    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerUrl)
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, connectorConsumerGroup)

    val kafkaConsumer = new KafkaConsumer[String, String](properties)

    val response = validAction match {
      case "start" => startConnector(connectServerBaseUrl, connectorConfig.toString())
      case "resume" => resumeConnector(connectServerBaseUrl, connectorName)
        /*if (connectServerVersion.startsWith("6.")) {
          println(s"Logging - WARN - Stopping a Kafka Connector is not possible in KafkaConnect ${connectServerVersion}")
          println("Logging - WARN - Instead deleting the KafkaConnector")
          startConnector(connectServerBaseUrl, connectorConfig.toString)
        } else {
          resumeConnector(connectServerBaseUrl, connectorName)
        }*/
      case "status" => statusOfConnector(connectServerBaseUrl, connectorName)
      case "describe" => describeConnector(connectServerBaseUrl, connectorName)
      case "stop" => stopConnector(connectServerBaseUrl, connectorName)
        /*if (connectServerVersion.startsWith("6.")) {
          println(s"Logging - WARN - Stopping a Kafka Connector is not possible in KafkaConnect ${connectServerVersion}")
          println("Logging - WARN - Instead deleting the KafkaConnector")
          deleteConnector(connectServerBaseUrl, connectorName)
        } else {
          stopConnector(connectServerBaseUrl, connectorName)
        }*/
      case "delete" => deleteConnector(connectServerBaseUrl, connectorName)
      case "get-offsets" => getCurrentOffsets(properties, connectorConsumerGroup)
      case "dry-run" => resetKafkaConsumerOffsetsDryRun(kafkaConsumer, topicName, epochTimestamp)
      case "execute" =>
        val topicPartitionOffsetAndTimestamp = getOffsetsForTimestamp(kafkaConsumer, topicName, epochTimestamp)
        val status = convertStringToJson(statusOfConnector(connectServerBaseUrl, connectorName))("connector")("state").str
        if (status.equals("RUNNING")) {
          if (connectServerVersion.startsWith("6.")) {
            deleteConnector(connectServerBaseUrl, connectorName)
            println("Connector deleted")
          } else {
            stopConnector(connectServerBaseUrl, connectorName)
            println("Connector stopped")
          }
        }

        resetConsumerOffsets(kafkaConsumer, topicPartitionOffsetAndTimestamp)

        if (connectServerVersion.startsWith("6.")) {
          startConnector(connectServerBaseUrl, connectorConfig.toString())
          println("Connector started")
        } else {
          resumeConnector(connectServerBaseUrl, connectorName)
          println("Connector resumed")
        }
        /*
        val topicPartitionOffsetAndTimestamp = getOffsetsForTimestamp(kafkaConsumer, topicName, epochTimestamp)
        if (action.equals("execute")) {
          println(s"Logging Stopping connector - ${connectorName}")
          val status = convertStringToJson(statusOfConnector(connectServerBaseUrl, connectorName))("connector")("state").str
          if (status.equals("RUNNING")) {
            if (connectServerVersion.startsWith("6.")) {
              deleteConnector(connectServerBaseUrl, connectorName)
              println("Connector deleted")
            } else {
              stopConnector(connectServerBaseUrl, connectorName)
              println("Connector stopped")
            }
          }
          resetConsumerOffsets(kafkaConsumer, topicPartitionOffsetAndTimestamp)

          if (connectServerVersion.startsWith("6.")) {
            startConnector(connectServerBaseUrl, connectorConfig.toString())
            println("Connector started")
          } else {
            resumeConnector(connectServerBaseUrl, connectorName)
            println("Connector resumed")
          }
        }
        printTopicPartitionsAndOffsets(topicPartitionOffsetAndTimestamp)
        */

      case _ =>
        config.printHelp()
    }

    println("Final Response - " + response)

  }

  private def resetKafkaConsumerOffsetsDryRun(kafkaConsumer: KafkaConsumer[String, String], topicName: String, epochTimestamp: Option[Long]): String = {
    val topicPartitionOffsetAndTimestamp = getOffsetsForTimestamp(kafkaConsumer, topicName, epochTimestamp)
    printTopicPartitionsAndOffsets(sortKafkaTopicPartitions(topicPartitionOffsetAndTimestamp))
  }


  private def getValidConnectServerUrl(connectServerUrl: String) = {
    val serverUrl = if (connectServerUrl.startsWith("http://")) {
      connectServerUrl
    } else {
      s"http://${connectServerUrl}"
    }
    if (serverUrl.endsWith("/")) {
      serverUrl
    } else {
      s"${serverUrl}/"
    }
  }

  private def printTopicPartitionsAndOffsets(topicPartitionOffsetAndTimestamp: Seq[(TopicPartition, OffsetAndTimestamp)]): String = {
    topicPartitionOffsetAndTimestamp
      .map { case (t, o) => (t, o.offset()) }
      .toString()
  }

  private def resetConsumerOffsets(kafkaConsumer: KafkaConsumer[String, String], topicPartitionOffsetAndTimestamp: util.Map[TopicPartition, OffsetAndTimestamp]): String = {
    val topicPartitions = topicPartitionOffsetAndTimestamp.keySet()
    kafkaConsumer.assign(topicPartitions)
    topicPartitionOffsetAndTimestamp.asScala.toList.foreach{ case(t, o) => kafkaConsumer.seek(t, o.offset())}
    kafkaConsumer.commitSync()
    kafkaConsumer.close()
    "Resetting Offsets - completed"
  }


  private def getCurrentOffsets(properties: Properties, connectorConsumerGroup: String): String = {
    properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "scala-admin-client")
    val adminClient = AdminClient.create(properties)
    val consumerGroupOffsetsList = adminClient.listConsumerGroupOffsets(connectorConsumerGroup)
    consumerGroupOffsetsList
      .partitionsToOffsetAndMetadata()
      .get()
      .asScala
      .toList
      .map{case (t, o) => (t, o.offset())}
      .toString()
  }

  private def getOffsetsForTimestamp(kafkaConsumer: KafkaConsumer[String, String], topicName: String, epochTimestamp: Option[Long]): util.Map[TopicPartition, OffsetAndTimestamp] = {
    val javaEpochTimestamp = epochTimestamp.get.asInstanceOf[java.lang.Long]
    val partitionsInfo = kafkaConsumer.partitionsFor(topicName)
    val topicPartitionsWithTimestamp = partitionsInfo
      .asScala
      .map(x => new TopicPartition(x.topic(), x.partition()) -> javaEpochTimestamp)
      .toMap
      .asJava
    kafkaConsumer.offsetsForTimes(topicPartitionsWithTimestamp)
  }

  private def startConnector(connectServerBaseUrl: String, connectorConfig: String): String = {
    val endPoint = if (connectServerBaseUrl.endsWith("/")) connectServerBaseUrl else connectServerBaseUrl + "/"
    println(s"End Point - $endPoint")
    println(s"Connector Config - $connectorConfig")
    getHttpResponse(s"${endPoint}connectors", "POST", connectorConfig)
  }

  private def statusOfConnector(connectServerBaseUrl: String, connectorName: String): String = {
    val endPoint = connectServerBaseUrl + "connectors/" + connectorName + "/status"
    println(s"Logging - Calling Endpoint - ${endPoint}")
    getHttpResponse(endPoint)
  }

  private def describeConnector(connectServerBaseUrl: String, connectorName: String): String = {
    val endPoint = connectServerBaseUrl + "connectors/" + connectorName
    println(s"Logging - Calling Endpoint - ${endPoint}")
    getHttpResponse(endPoint)
  }

  private def resumeConnector(connectServerBaseUrl: String, connectorName: String): String = {
    val endPoint = s"${connectServerBaseUrl}connectors/${connectorName}/resume"
    println(s"Logging - Calling Endpoint - ${endPoint}")
    getHttpResponse(endPoint, "PUT")
  }

  private def stopConnector(connectServerBaseUrl: String, connectorName: String): String = {
    val endPoint = s"${connectServerBaseUrl}connectors/${connectorName}/stop"
    println(s"Logging - Calling Endpoint - ${endPoint}")
    getHttpResponse(endPoint, "PUT")
  }

  private def deleteConnector(connectServerBaseUrl: String, connectorName: String): String = {
    val endPoint = s"${connectServerBaseUrl}connectors/${connectorName}"
    println(s"Logging - Calling Endpoint - ${endPoint}")
    getHttpResponse(endPoint, "DELETE")
  }

  private def getConnectServerVersion(connectServerUrl: String): String = {
    val jsonResponse = convertStringToJson(getHttpResponse(connectServerUrl))
    jsonResponse("version").str
  }

  def getHttpResponse(endPoint: String, requestMethod: String = "GET", connectorConfig: String = ""): String = {
    println(s"Endpoint -- ${endPoint}")
    val url = new URL(endPoint)
    val connection = url.openConnection().asInstanceOf[HttpURLConnection]
    connection.setRequestMethod(requestMethod)

    if (requestMethod.equals("POST")) {
      connection.setDoInput(true)
      connection.setDoOutput(true)
      connection.setRequestProperty("Content-Type", "application/json")
      connection.setRequestProperty("Accept", "application/json")

      val outputStream = new OutputStreamWriter(connection.getOutputStream)
      outputStream.write(connectorConfig)
      outputStream.flush()
    }

    val responseCode = connection.getResponseCode
    println(s"Response Code : - $responseCode")
    println(s"Response Message: ${connection.getResponseMessage}")
    responseCode match {
      case HTTP_OK => fromInputStream(connection.getInputStream).mkString
      case HTTP_NO_CONTENT => "NO CONTENT"
      case HTTP_ACCEPTED => "ACCEPTED"
      case HTTP_NOT_FOUND => "NOT FOUND"
      case _ => null
    }
  }

  def convertStringToJson(input: String): Value = {
    ujson.read(input)
  }

  private def sortKafkaTopicPartitions(topicPartitionOffsetsAndTimestamp: util.Map[TopicPartition, OffsetAndTimestamp]): Seq[(TopicPartition, OffsetAndTimestamp)] = {
    topicPartitionOffsetsAndTimestamp.asScala.toSeq.sortBy(x => x._1.partition())
  }
}
