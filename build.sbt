import NativePackagerHelper._
import com.typesafe.sbt.packager.docker.Cmd

name := "spark-docker"
scalaVersion := "2.11.12"
libraryDependencies ++= spark ++ logging

enablePlugins(JavaAppPackaging)
dockerUpdateLatest := true
Universal / mappings ++= contentOf(s"${(Compile / resourceDirectory).value}/spark-2.3.0-bin-hadoop2.7")
  .map { case (file, to) => file -> s"spark/$to" }
mainClass in Compile := Option("com.goyeau.spark.docker.Launcher")
dockerCommands :=
  dockerCommands.value.head +:
  Cmd("RUN", "apt-get update && apt-get install -y rsync") +:
  dockerCommands.value.tail

lazy val spark = {
  val version = "2.3.0"
  Seq(
    "org.apache.spark" %% "spark-sql" % version,
    "org.apache.spark" %% "spark-streaming" % version
  )
}

lazy val logging = Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)
