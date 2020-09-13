package com.bigdata.spark.listener

import org.apache.spark.SparkConf
import org.apache.spark.streaming.scheduler.{StreamingListener, StreamingListenerBatchCompleted, StreamingListenerOutputOperationCompleted}

class CustomStreamingListener(conf: SparkConf) extends StreamingListener {

  override def onBatchCompleted(batchCompleted: StreamingListenerBatchCompleted): Unit = {

  }
  override def onOutputOperationCompleted(outputOperationCompleted: StreamingListenerOutputOperationCompleted): Unit = {

  }
}
