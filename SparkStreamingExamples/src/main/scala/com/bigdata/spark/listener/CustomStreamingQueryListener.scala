package com.bigdata.spark.listener

import org.apache.spark.sql.streaming.StreamingQueryListener

class CustomStreamingQueryListener extends StreamingQueryListener {
  var aggValue = 0L

  def returnFinalCount(): Long = {
    aggValue
  }

  override def onQueryStarted(event: StreamingQueryListener.QueryStartedEvent): Unit = {

  }

  override def onQueryProgress(event: StreamingQueryListener.QueryProgressEvent): Unit = {
    aggValue = aggValue + event.progress.numInputRows
    println("Aggregated Value :: " + aggValue)
    println("Number of input rows : " + event.progress.numInputRows)
    println("Rows processed per seconds : " + event.progress.processedRowsPerSecond)
  }

  override def onQueryTerminated(event: StreamingQueryListener.QueryTerminatedEvent): Unit = {

  }
}
