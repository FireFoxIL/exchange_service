lazy val root = (project in file("."))
  .settings(
    name := "innodata_task",
    scalaVersion := "2.12.5",
    version := "0.1",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.1.0",
      "com.typesafe.akka" %% "akka-stream" % "2.5.11",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.0" ,
      "org.scalatest" %% "scalatest" % "3.0.5" % "test"
    )
  )