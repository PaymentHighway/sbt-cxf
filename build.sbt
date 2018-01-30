sbtPlugin := true

name := "sbt-cxf"

homepage := Some(new URL("https://github.com/solinor/sbt-cxf"))
startYear := Some(2016)
licenses := Seq(("Apache 2", new URL("http://www.apache.org/licenses/LICENSE-2.0.txt")))
organization := "io.paymenthighway.sbt"
organizationName := "Payment Highway Oy"
organizationHomepage := Some(url("https://paymenthighway.fi/en/"))

crossSbtVersions := Vector("0.13.16", "1.1.0")

developers := List(
  Developer("margussipria", "Margus Sipria", "margus+sbt-cxf@sipria.fi", url("https://github.com/margussipria"))
)

scmInfo := Some(ScmInfo(
  browseUrl = url("http://github.com/paymenthighway/sbt-cxf"),
  connection = "scm:git:https://github.com/paymenthighway/sbt-cxf.git",
  devConnection = Some("scm:git:git@github.com:paymenthighway/sbt-cxf.git")
))

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings"
)

scalaVersion := "2.10.6"
