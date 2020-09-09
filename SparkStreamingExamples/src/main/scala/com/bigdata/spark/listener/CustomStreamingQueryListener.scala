package com.bigdata.spark.listener

import org.apache.spark.sql.streaming.StreamingQueryListener

class CustomStreamingQueryListener extends StreamingQueryListener {
  override def onQueryStarted(event: StreamingQueryListener.QueryStartedEvent): Unit = {

  }

  override def onQueryProgress(event: StreamingQueryListener.QueryProgressEvent): Unit = {
    println("Number of input rows : " + event.progress.numInputRows)
    println("Rows processed per seconds : " + event.progress.inputRowsPerSecond)
  }

  override def onQueryTerminated(event: StreamingQueryListener.QueryTerminatedEvent): Unit = {

  }
}
