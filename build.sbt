sbtPlugin := true

name := "sbt-cxf"

homepage := Some(new URL("https://github.com/solinor/sbt-cxf"))

startYear := Some(2016)

licenses := Seq(("Apache 2", new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")))

organization := "com.solinor.sbt"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings"
)

scalaVersion := "2.10.6"

useGpg := true
