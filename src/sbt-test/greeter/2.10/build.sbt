name := "greeter"

scalaVersion := "2.11.8"

version := "1.0"

enablePlugins(CxfPlugin)

val CxfVersion = "3.1.14"

version in CXF := CxfVersion

defaultArgs in wsdl2java := Seq("-exsh", "true", "-validate")

wsdls in wsdl2java := Seq(Wsdl("HelloWorld", (resourceDirectory in Compile).value / "wsdl/HelloWorld.wsdl", Nil))
