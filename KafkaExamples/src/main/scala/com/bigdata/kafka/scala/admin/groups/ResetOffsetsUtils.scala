package com.bigdata.kafka.scala.admin.groups

import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer

import java.util.Properties

class ResetOffsetsUtils(config: Conf) {
  val propertiesFile = config.propertiesFile.toOption.get
  val bootstrapServerUrl = config.bootstrapServerUrl.toOption.get
  val connectServerUrl = config.connectServerUrl.toOption.get
  val action = config.action.toOption.get

  val connectorConfig = ujson.read(os.read(os.Path(propertiesFile)))
  val connectorName = connectorConfig("name").str
  val topicName = connectorConfig("config")("topics").str
  val connectorConsumerGroup = s"connect-${connectorName}"
  val connectServerBaseUrl = if (connectServerUrl.startsWith("http://") && connectServerUrl.endsWith("/")) connectServerUrl else s"http://${connectServerUrl}/"

  val properties = new Properties()

  properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerUrl)
  properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
  properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
  properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, connectorConsumerGroup)

  val kafkaConsumer = new KafkaConsumer[String, String](properties)

  def connectServerVersion: Unit = {

  }

}
