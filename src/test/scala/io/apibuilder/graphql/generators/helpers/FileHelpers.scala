package io.apibuilder.graphql.generators.helpers

import java.io.{File => JFile}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

trait FileHelpers {

  def writeToFile(path: String, contents: String): Unit = {
    val outputPath = Paths.get(path)
    val bytes = contents.getBytes(StandardCharsets.UTF_8)
    Files.write(outputPath, bytes)
    ()
  }

  def writeToFile(path: JFile, contents: String): Unit = {
    writeToFile(path.getAbsolutePath, contents)
  }

  def readFile(path: String): String = {
    readFile(new JFile(path))
  }

  def readFile(path: JFile): String = {
    val source = scala.io.Source.fromFile(path)
    try {
      source.getLines().mkString("\n")
    } finally {
      source.close()
    }
  }
}


