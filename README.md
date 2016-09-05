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

addSbtPlugin("com.solinor.sbt" % "sbt-cxf" % "1.1-SNAPSHOT")
```

Add WSDL file (example HelloWorld.wsdl) to wsdl subdirectory under resources directory

Add the plugin configuration in build.sbt:
```scala
enablePlugins(com.solinor.sbt.cxf.CxfPlugin)

val CxfVersion = "3.1.7"

cxfVersion in (Compile, wsdl2java) := CxfVersion

defaultArgs in (Compile, wsdl2java) := Seq("-exsh", "true", "-validate") // If this is acceptable, this can be omitted

wsdls in (Compile, wsdl2java) := Seq(
  Wsdl("HelloWorld", (resourceDirectory in Compile).value / "wsdl/HelloWorld.wsdl", Nil)
)
```
