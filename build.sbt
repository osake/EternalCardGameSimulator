import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.1",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Run",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
    libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.22",
    libraryDependencies += "com.google.code.gson" % "gson" % "2.8.0",
    libraryDependencies += "org.backuity" %% "ansi-interpolator" % "1.1.0" % "provided",
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.17"
  )
