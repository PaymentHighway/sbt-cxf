name := "greeter"

scalaVersion := "2.11.8"

version := "1.0"

wsdls := Seq(Wsdl("HelloWorld", (resourceDirectory in Compile).value / "wsdl/HelloWorld.wsdl", Nil))
