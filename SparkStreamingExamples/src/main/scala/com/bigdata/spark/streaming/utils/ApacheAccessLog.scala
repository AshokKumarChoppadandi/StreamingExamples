package com.bigdata.spark.streaming.utils

case class ApacheAccessLog(ipAddress: String, clientIdentd: String, userID: String, dateTimeString: String, method: String, endPoint: String, protocol: String, responseCode: Int, contentSize: Long, referer: String, userAgent: String)