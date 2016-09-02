sbt-cxf
========

sbt-cxf is a plugin for SBT that will generate java-classes for web-service WSDLs using Apache CXF. Find out more about [Apache CXF](http://cxf.apache.org/).

## How to use

Add the plugin in project/plugins.sbt:
```scala
resolvers ++= Seq(
	"Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
	"Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

addSbtPlugin("com.solinor.sbt" % "sbt-cxf" % "1.0-SNAPSHOT")
```

Add the plugin configuration in build.sbt:
```scala
wsdls := Seq(
  wsdls := Seq(Wsdl("HelloWorld", file("src/main/wsdl/HelloWorld.wsdl"), Nil)) // Example
)
```
