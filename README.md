sbt-cxf
=======

[![][Build Status img]][Build Status]

sbt-cxf is a plugin for SBT that will generate java-classes for web-service WSDLs using Apache CXF. Find out more about [Apache CXF](http://cxf.apache.org/).

## How to use

Add the plugin in project/plugins.sbt:
```scala
resolvers ++= Seq(
  "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

addSbtPlugin("io.paymenthighway.sbt" % "sbt-cxf" % "1.6")
```

Add WSDL file (example HelloWorld.wsdl) to wsdl subdirectory under resources directory

Add the plugin configuration in build.sbt:
```scala
enablePlugins(CxfPlugin)

val CxfVersion = "3.3.3"

version in CXF := CxfVersion

cxfDefaultArgs := Seq("-exsh", "true", "-validate") // If this is acceptable, this can be omitted

cxfWSDLs := Seq(
  Wsdl("HelloWorld", (resourceDirectory in Compile).value / "wsdl/HelloWorld.wsdl", Nil)
)
```

[Build Status]:https://travis-ci.org/PaymentHighway/sbt-cxf
[Build Status img]:https://travis-ci.org/PaymentHighway/sbt-cxf.svg?branch=master
