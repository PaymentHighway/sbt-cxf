sbt-cxf
=======

sbt-cxf is a plugin for SBT that will generate java-classes for web-service WSDLs using Apache CXF. Find out more about [Apache CXF](http://cxf.apache.org/).

## How to use

Add the plugin in project/plugins.sbt:
```scala
resolvers ++= Seq(
  "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

addSbtPlugin("io.paymenthighway.sbt" % "sbt-cxf" % "1.4")
```

Add WSDL file (example HelloWorld.wsdl) to wsdl subdirectory under resources directory

Add the plugin configuration in build.sbt:
```scala
enablePlugins(io.paymenthighway.sbt.cxf.CxfPlugin)

val CxfVersion = "3.1.14"

version in CXF := CxfVersion

defaultArgs in wsdl2java := Seq("-exsh", "true", "-validate") // If this is acceptable, this can be omitted

wsdls in wsdl2java := Seq(
  Wsdl("HelloWorld", (resourceDirectory in Compile).value / "wsdl/HelloWorld.wsdl", Nil)
)
```
