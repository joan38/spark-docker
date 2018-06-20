package com.goyeau.spark.docker

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object Streaming {
  def run(sparkConf: SparkConf): Unit = {
    val streamingContext = new StreamingContext(sparkConf, Seconds(1))

    val lines = streamingContext.socketTextStream("10.10.9.141", 9999)
    lines.print()

    streamingContext.start()
    streamingContext.awaitTermination()
  }
}
