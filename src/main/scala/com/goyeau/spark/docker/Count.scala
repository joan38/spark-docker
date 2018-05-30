package com.goyeau.spark.docker

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object Count {
  def run(sparkConf: SparkConf): Unit = {
    val session = SparkSession.builder.config(sparkConf).getOrCreate()
    import session.implicits._

    val data = session.sqlContext.createDataset(Seq("oh", "ah", "yeah"))
    val result = data.count()
    println("result: " + result)

    session.stop()
  }
}
