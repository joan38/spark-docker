package com.goyeau.spark.docker

import buildinfo.BuildInfo

import scala.concurrent.duration._
import sys.process._
import org.apache.spark.SparkConf

object Launcher extends App {
  sys.env.get("ROLE") match {
    case Some("master") => startMaster()
    case Some("slave")  => startSlave()
    case _              => throw new IllegalArgumentException("ROLE should be master or slave")
  }

  def startMaster(): Unit = {
    if ("spark/sbin/start-master.sh".! != 0) throw new Exception("Starting master failed")
    sys.addShutdownHook("spark/sbin/stop-master.sh".!)

    val sparkConf = new SparkConf()
      .setMaster("spark://localhost:7077")
      .setAppName("Simple Application")
      .setJars(Seq(s"lib/${BuildInfo.assembly_assemblyJarName}"))

//    Count.run(sparkConf)
    Streaming.run(sparkConf)
  }

  def startSlave(): Unit = {
    val masterUri = sys.env.getOrElse(
      "SPARK_MASTER",
      throw new IllegalArgumentException("SPARK_MASTER should be given as for example: spark://master:7077")
    )

    if (s"spark/sbin/start-slave.sh $masterUri".! != 0) throw new Exception("Starting slave failed")
    sys.addShutdownHook("spark/sbin/stop-slave.sh".!)
    while (true) Thread.sleep(1.day.toMillis)
  }
}
