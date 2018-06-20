import NativePackagerHelper._
import com.typesafe.sbt.packager.docker.Cmd

name := "spark-docker"
scalaVersion := "2.11.12"
libraryDependencies ++= spark ++ logging

enablePlugins(JavaAppPackaging, BuildInfoPlugin)

assembly / assemblyMergeStrategy := { x =>
  val oldStrategy = (assembly / assemblyMergeStrategy).value
  val strategy = oldStrategy(x)
  if (strategy == MergeStrategy.deduplicate) MergeStrategy.first
  else strategy
}
buildInfoKeys += BuildInfoKey(assembly / assemblyJarName)
Universal / mappings += (assembly.value -> s"lib/${(assembly / assemblyJarName).value}")

dockerUpdateLatest := true
Universal / mappings ++=
  contentOf(s"${(Compile / resourceDirectory).value}/spark-2.3.1-bin-hadoop2.7")
    .map { case (file, to) => file -> s"spark/$to" }
mainClass in Compile := Option("com.goyeau.spark.docker.Launcher")
dockerCommands :=
  dockerCommands.value.head +:
    Cmd("RUN", "apt-get update && apt-get install -y rsync") +:
    dockerCommands.value.tail

lazy val spark = {
  val version = "2.3.1"
  Seq(
    "org.apache.spark" %% "spark-sql" % version,
    "org.apache.spark" %% "spark-streaming" % version,
    "org.apache.hadoop" % "hadoop-aws" % "2.7.3",
    "com.amazonaws" % "aws-java-sdk" % "1.7.4"
  )
}

lazy val logging = Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)
