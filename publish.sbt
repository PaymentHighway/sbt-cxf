credentials += Credentials(Path.userHome / ".sbt" / "sonatype-credentials")

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) {
    Some("Sonatype Nexus Snapshots" at s"${nexus}content/repositories/snapshots")
  } else {
    Some("Sonatype Nexus Staging" at s"${nexus}service/local/staging/deploy/maven2")
  }
}

useGpg := true
