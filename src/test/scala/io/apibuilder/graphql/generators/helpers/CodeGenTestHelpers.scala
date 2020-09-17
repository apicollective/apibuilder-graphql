package io.apibuilder.graphql.generators.helpers

import java.io.{File => JFile}

/**
 * Utility to compare a string to an expected value stored in a file
 */
trait CodeGenTestHelpers extends FileHelpers {
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
    // if (!f.exists()) { s"touch ${f.getAbsolutePath}".! }
    if (!f.exists()) {
      sys.error(s"Could not find file $localPath. Expected it at ${f.getAbsolutePath}")
    }
    f
  }

}


