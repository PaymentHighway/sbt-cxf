name := "greeter"

scalaVersion := "2.12.5"

version := "1.0"

enablePlugins(CxfPlugin)

val CxfVersion = "3.2.4"

version in CXF := CxfVersion

cxfDefaultArgs in Test := Seq("-exsh", "true", "-validate")

cxfWSDLs in Test := Seq(Wsdl("HelloWorld", (resourceDirectory in Test).value / "wsdl/HelloWorld.wsdl", Nil))
