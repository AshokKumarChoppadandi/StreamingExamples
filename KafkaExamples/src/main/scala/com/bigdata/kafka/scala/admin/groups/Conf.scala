package com.bigdata.kafka.scala.admin.groups

import org.rogach.scallop.ScallopConf

class Conf(args: Array[String]) extends ScallopConf(args) {
  val propertiesFile = opt[String]("propertiesFile", required = true, descr = "Local path to JSON properties file")
  val bootstrapServerUrl = opt[String]("bootstrapServerUrl", required = true, descr = "Kafka Cluster Bootstrap Server URL with port number")
  val connectServerUrl = opt[String]("connectServerUrl", required = true, descr = "Kafka Connect Server URL with http:// prefix with port number")
  val epochTimestamp = opt[Long]("epochTimestamp", descr = "Epoch timestamp in milliseconds to reset Kafka Connector Offsets")
  val action = opt[String]("action", required = true, descr = "Action to perform. Support actions are start | status | describe | stop | resume | delete | dryRun | execute")

  verify()
}
