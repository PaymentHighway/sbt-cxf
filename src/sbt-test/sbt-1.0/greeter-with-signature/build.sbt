name := "greeter"

scalaVersion := "2.13.0"

version := "1.0"

enablePlugins(CxfPlugin)

val CxfVersion = "3.3.2"

version in CXF := CxfVersion

cxfDefaultArgs := Seq("-exsh", "true", "-validate")

cxfWSDLs := Seq(
  Wsdl("HelloWorld", (resourceDirectory in Compile).value / "wsdl/HelloWorld.wsdl", Seq(
    "-wsdlLocation", "classpath:wsdl/HelloWorld.wsdl",
    "-b", ((resourceDirectory in Compile).value / "wsdl/bindings.xjb").getPath
  ))
)

Compile / excludeFilter in cxfGenerate := (Compile / cxfExcludeFilter).value {
  case file if file.startsWith("org/apache/xml/security/binding/xmldsig") => true
}

libraryDependencies ++= Seq(
  "org.apache.santuario" % "xmlsec" % "2.1.4"
)
