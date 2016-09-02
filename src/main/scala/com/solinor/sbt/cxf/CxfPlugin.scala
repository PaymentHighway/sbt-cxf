package com.solinor.sbt.cxf

import java.io.File
import java.net.URLClassLoader

import sbt.Keys._
import sbt.classpath.ClasspathUtilities
import sbt.{SettingKey, TaskKey, _}

object CxfPlugin extends AutoPlugin {

  object Import {
    val cxf = config("cxf").hide

    val cxfVersion = SettingKey[String]("cxfVersion", "Use this version of cxf")

    val wsdls = SettingKey[Seq[Wsdl]]("wsdls", "wsdls to generate java files from")

    val wsdl2java = TaskKey[Seq[File]]("wsdl2java", "Generates java files from wsdls")
    val wsdl2javaDefaultArgs = SettingKey[Seq[String]]("wsdl2java default arguments")

    val useKeyAsPartOfOutputDirectory = SettingKey[Boolean]("wsdl2java use key for output directory")

    case class Wsdl(key: String, file: File, args: Seq[String])
  }

  override val trigger = allRequirements
  override val requires = sbt.plugins.IvyPlugin && sbt.plugins.JvmPlugin

  val autoImport = Import

  import autoImport._

  override def projectSettings = Seq(
    ivyConfigurations += cxf,
    cxfVersion := "3.1.7",
    libraryDependencies <++= cxfVersion { version => Seq[ModuleID](
      "org.apache.cxf" % "cxf-tools-wsdlto-core" % version % cxf,
      "org.apache.cxf" % "cxf-tools-wsdlto-databinding-jaxb" % version % cxf,
      "org.apache.cxf" % "cxf-tools-wsdlto-frontend-jaxws" % version % cxf
    )},
    wsdls := Nil,
    managedClasspath in wsdl2java <<= (classpathTypes in wsdl2java, update) map { (ct, report) =>
      Classpaths.managedJars(cxf, ct, report)
    },
    sourceManaged in wsdl2java <<= sourceManaged(_ / "cxf"),

    managedSourceDirectories in Compile <++= (
      wsdls,
      sourceManaged in wsdl2java,
      useKeyAsPartOfOutputDirectory in wsdl2java
    ) { (wsdls, basedir, useKey) =>
      if (useKey) {
        wsdls.map { wsdl => outputDirectory(basedir, wsdl.key) }
      } else {
        Seq(basedir)
      }
    },
    wsdl2java <<= (
      wsdls,
      sourceManaged in wsdl2java,
      managedClasspath in wsdl2java,
      wsdl2javaDefaultArgs in wsdl2java,
      useKeyAsPartOfOutputDirectory in wsdl2java,
      streams in Compile
    ) map { (wsdls, basedir, cp, defaultArgs, useKey, stream) =>
      val classpath = cp.files

      if (!basedir.exists() || wsdls.exists(_.file.lastModified > basedir.lastModified())) {
        if (basedir.exists()) {
          stream.log.info("Removing output directory...")
          IO.delete(basedir)
        }
        IO.createDirectory(basedir)

        val classLoader = ClasspathUtilities.toLoader(classpath).asInstanceOf[URLClassLoader]

        val WSDLToJava = classLoader.loadClass("org.apache.cxf.tools.wsdlto.WSDLToJava")
        val ToolContext = classLoader.loadClass("org.apache.cxf.tools.common.ToolContext")

        val oldContextClassLoader = Thread.currentThread.getContextClassLoader

        try {
          Thread.currentThread.setContextClassLoader(classLoader)

          wsdls.flatMap { wsdl =>
            val output = if (useKey) outputDirectory(basedir, wsdl.key) else basedir

            val args = Seq("-d", output.getAbsolutePath) ++ defaultArgs ++ wsdl.args :+ wsdl.file.getAbsolutePath
            callWsdl2java(wsdl.key, output, args, classpath, stream.log)(WSDLToJava, ToolContext)

            (output ** "*.java").get
          }.distinct
        } catch { case e: Throwable =>
          stream.log.error("Failed to compile wsdl with exception: " + e.getMessage)
          stream.log.trace(e)

          (basedir ** "*.java").get
        } finally {
          Thread.currentThread.setContextClassLoader(oldContextClassLoader)

          classLoader.close()
        }
      } else {
        (basedir ** "*.java").get
      }
    },
    wsdl2javaDefaultArgs := Seq("-exsh", "true", "-validate"),

    sourceGenerators in Compile += wsdl2java.taskValue,

    useKeyAsPartOfOutputDirectory := false
  )

  private def outputDirectory(basedir: File, key: String) = new File(basedir, key).getAbsoluteFile

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
