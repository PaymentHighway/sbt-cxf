credentials += Credentials(Path.userHome / ".sbt" / "sonatype-credentials")

publishTo <<= version { version: String =>
  if (version.trim.endsWith("SNAPSHOT")) {
    Some("Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
  } else {
    Some("Sonatype Nexus Staging" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
  }
}

pomExtra <<= (pomExtra, name, description) { (pom, name, desc) => pom ++ xml.Group(
  <scm>
    <url>http://github.com/solinor/sbt-cxf</url>
    <connection>scm:git:git://github.com/solinor/sbt-cxf.git</connection>
    <developerConnection>scm:git:git@github.com:solinor/sbt-cxf.git</developerConnection>
  </scm>
  <developers>
    <developer>
      <id>margus.sipria</id>
      <name>Margus Sipria</name>
      <url>https://github.com/margussipria</url>
    </developer>
  </developers>
)}
