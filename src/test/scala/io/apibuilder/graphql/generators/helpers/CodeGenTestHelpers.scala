package io.apibuilder.graphql.generators.helpers

import java.io.{File => JFile}

/**
 * Utility to compare a string to an expected value stored in a file
 */
trait CodeGenTestHelpers extends FileHelpers {
  private[this] val Overwrite = true

  def codeGenTestHelpersDir: String

  def mustMatchFile(fileName: String, actual: String): Unit = {
    val expectedPath = resolvePath(fileName)
    val expected = readFile(expectedPath).trim
    val tmpPath = new JFile(expectedPath.getAbsolutePath + ".actual")

    if (expected == actual.trim) {
      if (tmpPath.exists) {
        tmpPath.delete()
        ()
      }
    } else {
      if (Overwrite) {
        writeToFile(expectedPath.getAbsolutePath, actual.trim)
        println(s"Updated generated code in ${expectedPath}")
      } else {
        writeToFile(tmpPath, actual.trim)
        sys.error(
          s"Generated code does not match expected code in file ${expectedPath}:" +
            s"diff ${expectedPath} ${tmpPath}"
        )
      }
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
    if (Overwrite && !f.exists()) { s"touch ${f.getAbsolutePath}".! }
    if (!f.exists()) {
      sys.error(s"Could not find file $localPath. Expected it at ${f.getAbsolutePath}")
    }
    f
  }

}


