{
  val pluginVersion = Option(System.getProperty("plugin.version")) getOrElse {
    throw new RuntimeException("The system property 'plugin.version' is not defined. Specify this property using the scriptedLaunchOpts -D.")
  }
  addSbtPlugin("com.solinor.sbt" % "sbt-cxf" % pluginVersion)
}
