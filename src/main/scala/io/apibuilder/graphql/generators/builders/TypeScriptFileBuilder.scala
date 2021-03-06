package io.apibuilder.graphql.generators.builders

import io.apibuilder.graphql.util.Text

object TypeScriptFile {

  def joinWithComma(all: Seq[String]): String = {
    all.map { f =>
      if (f.endsWith(",")) {
        f.dropRight(1)
      } else {
        f
      }
    }.mkString(",\n\n")
  }

  def wrapWithDefaultExport(all: Seq[String]): String = {
    Seq(
      "export default {",
      Text.indent(joinWithComma(all)),
      "}",
    ).mkString("\n")
  }

  def toImportStatement(imp: String): String = {
    s"""import $imp from "../graphql/$imp";"""
  }

}

case class TypeScriptFile(
  addDefaultExport: Boolean,
  imports: Seq[String],
  fragments: Seq[String],
) {
  import TypeScriptFile._

  private[this] lazy val sortedFragments = {
    fragments.sortBy { f =>
      val index = if (f.startsWith("Query")) {
        0
      } else if (f.startsWith("Mutation")) {
        1
      } else {
        2
      }
      (index, f.toLowerCase)
    }
  }

  val formatted: String = {
    val finalFragments = if (addDefaultExport) {
      Seq(wrapWithDefaultExport(sortedFragments))
    } else {
      sortedFragments
    }

    Seq(
      imports.distinct.map(toImportStatement).sorted.mkString("\n"),
      joinWithComma(finalFragments),
    ).filterNot(_.isEmpty).mkString("\n\n")
  }

  def merge(other: TypeScriptFile): TypeScriptFile = {
    assert(
      this.addDefaultExport == other.addDefaultExport,
      s"Files to merge must have the same ${addDefaultExport} value"
    )
    this.copy(
      imports = this.imports ++ other.imports,
      fragments = this.fragments ++ other.fragments,
    )
  }
}

// Note: NOT thread safe
case class TypeScriptFileBuilder() {
  private var addDefaultExport = false
  private val imports = collection.mutable.ListBuffer[String]()
  private val fragments = collection.mutable.ListBuffer[String]()

  def add(builder: TypeScriptFile): Unit =  {
    builder.imports.foreach { imp => addImport(imp) }
    builder.fragments.foreach { fragment => addFragment(fragment) }
  }

  def addImport(imp: String): Unit = {
    imports.append(imp)
  }

  def addFragment(fragment: String): Unit =  {
    fragments.append(fragment)
  }

  def setWrapWithDefaultExport(addDefaultExport: Boolean): Unit = {
    this.addDefaultExport = addDefaultExport;
  }

  def wrapContentWithObject(name: String): Unit = {
    setFragments(
      Seq(
        s"$name: {",
        Text.indent(TypeScriptFile.joinWithComma(fragments.toSeq)),
        "},",
      ).mkString("\n")
    )
  }

  def setFragments(fragment: String): Unit = {
    this.fragments.clear()
    this.fragments.addOne(fragment)
    ()
  }

  def isEmpty: Boolean = {
    imports.isEmpty && fragments.isEmpty
  }

  def nonEmpty: Boolean = !isEmpty

  def build(): Option[TypeScriptFile] = {
    if (isEmpty) {
      None
    } else {
      Some(
        TypeScriptFile(
          addDefaultExport = addDefaultExport,
          imports = imports.toSeq,
          fragments = fragments.toSeq,
        )
      )
    }
  }

}
