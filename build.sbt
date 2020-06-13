
name := "spark-sql-test"

version := "0.1"

scalaVersion := "2.11.12"

assemblyJarName in assembly := "spark-sql-v01.jar"

mainClass in assembly := Some("org.example.App")

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % "2.3.0",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}