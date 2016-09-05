package com.solinor.sbt.cxf

import java.io.File
import java.net.URLClassLoader

import sbt.Keys._
import sbt.classpath.ClasspathUtilities
import sbt.{SettingKey, TaskKey, _}

object CxfPlugin extends AutoPlugin {

  object Import {
    val cxf = config("cxf").hide

    lazy val wsdls = SettingKey[Seq[Wsdl]]("wsdls", "wsdls to generate java files from")

    lazy val wsdl2java = TaskKey[Seq[File]]("wsdl2java", "Generates java files from wsdls")
    lazy val generate = TaskKey[Seq[File]]("wsdl2java-generate")

    lazy val defaultArgs = SettingKey[Seq[String]]("wsdl2java default arguments")

    case class Wsdl(key: String, file: File, args: Seq[String])
  }

  override val trigger = noTrigger
  override val requires = sbt.plugins.IvyPlugin && sbt.plugins.JvmPlugin

  val autoImport = Import

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = baseProjectSettings

  lazy val baseProjectSettings = Seq(
    ivyConfigurations += cxf,

    libraryDependencies <++= (version in cxf) { version => Seq[ModuleID](
      "org.apache.cxf" % "cxf-tools-wsdlto-core" % version % cxf,
      "org.apache.cxf" % "cxf-tools-wsdlto-databinding-jaxb" % version % cxf,
      "org.apache.cxf" % "cxf-tools-wsdlto-frontend-jaxws" % version % cxf
    )},

    wsdl2java := (generate in wsdl2java).value,

    // Enable this when IntelliJ IDEA does not threat it as part of namespace!
    sourceManaged in cxf <<= (sourceManaged in Compile), // (_ / "cxf")

    managedClasspath in wsdl2java <<= (classpathTypes in wsdl2java, update) map { (ct, report) =>
      Classpaths.managedJars(cxf, ct, report)
    },

    version in cxf := "3.1.7",

    sourceGenerators in Compile += wsdl2java.taskValue
  ) ++ inTask(wsdl2java)(Seq(
    generate := {
      val s = streams.value

      val basedir = (sourceManaged in cxf).value
      val classpath = (managedClasspath in wsdl2java).value.files

      if (wsdls.value.nonEmpty && (!basedir.exists() || wsdls.value.exists(_.file.lastModified > basedir.lastModified()))) {
        if (basedir.exists()) {
          s.log.info("Removing output directory...")
          clean.value
        }
        IO.createDirectory(basedir)

        val classLoader = ClasspathUtilities.toLoader(classpath).asInstanceOf[URLClassLoader]

        val WSDLToJava = classLoader.loadClass("org.apache.cxf.tools.wsdlto.WSDLToJava")
        val ToolContext = classLoader.loadClass("org.apache.cxf.tools.common.ToolContext")

        val oldContextClassLoader = Thread.currentThread.getContextClassLoader

        try {
          Thread.currentThread.setContextClassLoader(classLoader)

          wsdls.value.flatMap { wsdl =>
            val args = Seq("-d", basedir.getAbsolutePath) ++ (defaultArgs in wsdl2java).value ++ wsdl.args :+ wsdl.file.getAbsolutePath
            callWsdl2java(wsdl.key, basedir, args, classpath, s.log)(WSDLToJava, ToolContext)

            (basedir ** "*.java").get
          }.distinct
        } catch { case e: Throwable =>
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
    },

    clean := IO.delete((sourceManaged.value ** "*").get),

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
