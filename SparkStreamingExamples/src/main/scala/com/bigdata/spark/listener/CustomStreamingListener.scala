package com.bigdata.spark.listener

import org.apache.spark.streaming.scheduler.{StreamingListener, StreamingListenerBatchCompleted, StreamingListenerOutputOperationCompleted}

class CustomStreamingListener extends StreamingListener {
  override def onBatchCompleted(batchCompleted: StreamingListenerBatchCompleted): Unit = {
    println("Batch Completed : " + batchCompleted.batchInfo.numRecords)
  }

  override def onOutputOperationCompleted(outputOperationCompleted: StreamingListenerOutputOperationCompleted): Unit = {
    println("Output Operation Completed Completed : " + outputOperationCompleted.outputOperationInfo.duration)
  }
}

