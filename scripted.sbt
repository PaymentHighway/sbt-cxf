scriptedLaunchOpts ++= Seq(
  "-Xmx1024M",
  "-XX:MaxPermSize=256M",
  s"-Dplugin.version=${(version in ThisBuild).value}"
)
