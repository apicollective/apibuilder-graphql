package io.apibuilder.graphql.generators.helpers

import java.io.{File => JFile}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

/**
 * Utility to compare a string to an expected value stored in a file
 */
trait CodeGenTestHelpers {
  def codeGenTestHelpersDir: String

  def mustMatchFile(fileName: String, actual: String): Unit = {
    val expectedPath = resolvePath(fileName)
    val expected = readFile(expectedPath).trim
    if (expected != actual.trim) {
      val tmpPath = expectedPath.getAbsolutePath + ".actual"
      writeToFile(tmpPath, actual.trim)
      sys.error(
        s"Generated code does not match expected code in file ${expectedPath}:" +
          s"diff ${expectedPath} ${tmpPath}"
      )
    }
  }

  def writeToFile(path: String, contents: String): Unit = {
    val outputPath = Paths.get(path)
    val bytes = contents.getBytes(StandardCharsets.UTF_8)
    Files.write(outputPath, bytes)
    ()
  }

  /**
   * Finds the actual source file for the resource with this name. We
   * need this to easily overwrite the resource when updating our
   * generated code.
   */
  private[this] def resolvePath(filename: String): JFile = {
    import sys.process._
    val pwd = "pwd".!!.trim
    val localPath = s"$codeGenTestHelpersDir/$filename"
    val f = new JFile(s"$pwd/src/test/resources/$localPath")
    if (!f.exists()) {
      sys.error(s"Could not find file $localPath. Expected it at ${f.getAbsolutePath}")
    }
    f
  }

  private[this] def readFile(path: JFile): String = {
    val source = scala.io.Source.fromFile(path)
    try {
      source.getLines().mkString("\n")
    } finally {
      source.close()
    }
  }
}


