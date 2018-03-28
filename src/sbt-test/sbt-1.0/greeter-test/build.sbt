name := "greeter"

scalaVersion := "2.12.5"

version := "1.0"

enablePlugins(CxfPlugin)

val CxfVersion = "3.2.4"

version in CXF := CxfVersion

Test / cxfDefaultArgs := Seq("-exsh", "true", "-validate")

Test / cxfWSDLs := Seq(Wsdl("HelloWorld", (resourceDirectory in Test).value / "wsdl/HelloWorld.wsdl", Nil))
