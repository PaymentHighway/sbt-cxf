package io.paymenthighway.sbt.cxf

import java.io.File
import java.net.URLClassLoader

import sbt.Keys._
import sbt.{SettingKey, TaskKey, _}

object CxfPlugin extends AutoPlugin {

  object Import {
    val CXF = config("CXF").hide

    lazy val wsdls = SettingKey[Seq[Wsdl]]("wsdls", "wsdls to generate java files from")

    lazy val wsdl2java = TaskKey[Seq[File]]("wsdl2java", "Generates java files from wsdls")
    lazy val generate = TaskKey[Seq[File]]("wsdl2java-generate")

    lazy val defaultArgs = SettingKey[Seq[String]]("wsdl2java default arguments")

    case class Wsdl(key: String, file: File, args: Seq[String])
  }

  override val trigger = noTrigger
  override val requires = sbt.plugins.IvyPlugin && sbt.plugins.JvmPlugin

  val autoImport: Import.type = Import

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = baseProjectSettings

  lazy val baseProjectSettings: Seq[Def.Setting[_]] = Seq(
    ivyConfigurations += CXF,

    libraryDependencies ++= Seq(
      "org.apache.cxf" % "cxf-tools-wsdlto-core" % (version in CXF).value % CXF,
      "org.apache.cxf" % "cxf-tools-wsdlto-databinding-jaxb" % (version in CXF).value % CXF,
      "org.apache.cxf" % "cxf-tools-wsdlto-frontend-jaxws" % (version in CXF).value % CXF
    ),

    wsdl2java := (generate in wsdl2java).value,

    // Enable this when IntelliJ IDEA does not threat it as part of namespace!
    sourceManaged in wsdl2java := (sourceManaged in Compile).value / "cxf",

    managedClasspath in wsdl2java := {
      Classpaths.managedJars(CXF, (classpathTypes in wsdl2java).value, update.value)
    },

    version in CXF := "3.1.14",

    sourceGenerators in Compile += wsdl2java.taskValue
  ) ++ inTask(wsdl2java)(Seq(
    generate := Def.taskDyn {
      val s = streams.value

      val basedir = sourceManaged.value
      val classpath = managedClasspath.value.files

      val wsdlFiles = wsdls.value

      Def.task {
        if (wsdlFiles.nonEmpty && (!basedir.exists() || wsdlFiles.exists(_.file.lastModified() > basedir.lastModified()))) {
          if (basedir.exists()) {
            s.log.info("Removing output directory...")
            IO.delete(basedir)
          }
          IO.createDirectory(basedir)

          val classLoader = new URLClassLoader(Path.toURLs(classpath), getClass.getClassLoader)

          val WSDLToJava = classLoader.loadClass("org.apache.cxf.tools.wsdlto.WSDLToJava")
          val ToolContext = classLoader.loadClass("org.apache.cxf.tools.common.ToolContext")

          val oldContextClassLoader = Thread.currentThread.getContextClassLoader

          try {
            Thread.currentThread.setContextClassLoader(classLoader)

            wsdlFiles.flatMap { wsdl =>
              val args = Seq("-d", basedir.getAbsolutePath) ++ (defaultArgs in wsdl2java).value ++ wsdl.args :+ wsdl.file.getAbsolutePath
              callWsdl2java(wsdl.key, basedir, args, classpath, s.log)(WSDLToJava, ToolContext)

              (basedir ** "*.java").get
            }.distinct
          } catch {
            case e: Throwable =>
              s.log.error("Failed to compile wsdl with exception: " + e.getMessage)
              s.log.trace(e)

              (basedir ** "*.java").get
          } finally {
            Thread.currentThread.setContextClassLoader(oldContextClassLoader)

            classLoader.close()
          }
        } else {
          (basedir ** "*.java").get
        }
      }
    }.value,

    clean := IO.delete(sourceManaged.value),

    wsdls := Nil,

    defaultArgs := Seq("-exsh", "true", "-validate")
  ))

  private def callWsdl2java(key: String, output: File, arguments: Seq[String], classpath: Seq[File], logger: Logger)(
    WSDLToJava: Class[_],
    ToolContext: Class[_]
  ) {
    logger.info("WSDL: key=" + key + ", args=" + arguments.mkString(" "))
    logger.info("Compiling WSDL...")

    val start = System.currentTimeMillis()

    val constructor = WSDLToJava.getConstructor(classOf[Array[String]])
    val run = WSDLToJava.getMethod("run", ToolContext)

    try {
      val instance = constructor.newInstance(arguments.toArray)
      run.invoke(instance, ToolContext.newInstance().asInstanceOf[AnyRef])
    } catch { case e: Throwable =>
      logger.error("Failed to compile wsdl with exception: " + e.getMessage)
      logger.trace(e)
    } finally {
      val end = System.currentTimeMillis()
      logger.info("Compiled WSDL in " + (end - start) + "ms.")
    }
  }
}
