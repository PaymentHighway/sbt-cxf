name := "greeter"

scalaVersion := "2.11.8"

version := "1.0"

enablePlugins(com.solinor.sbt.cxf.CxfPlugin)

val CxfVersion = "3.1.7"

cxfVersion in (Compile, wsdl2java) := CxfVersion

defaultArgs in (Compile, wsdl2java) := Seq("-exsh", "true", "-validate")

wsdls in (Compile, wsdl2java) := Seq(Wsdl("HelloWorld", (resourceDirectory in Compile).value / "wsdl/HelloWorld.wsdl", Nil))
