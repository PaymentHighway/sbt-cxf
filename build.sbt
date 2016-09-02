sbtPlugin := true

name := "sbt-cxf"

homepage := Some(new URL("https://github.com/solinor/sbt-cxf"))
startYear := Some(2016)
licenses := Seq(("Apache 2", new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")))
organization := "com.solinor.sbt"
organizationName := "Solinor Oy"
organizationHomepage := Some(url("https://solinor.com/"))

developers := List(
  Developer("margussipria", "Margus Sipria", "margus+sbt-cxf@sipria.fi", url("https://github.com/margussipria"))
)

scmInfo := Some(ScmInfo(
  browseUrl = url("http://github.com/solinor/sbt-cxf"),
  connection = "scm:git:https://github.com/solinor/sbt-cxf.git",
  devConnection = Some("scm:git:git@github.com:solinor/sbt-cxf.git")
))

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings"
)

scalaVersion := "2.10.6"
