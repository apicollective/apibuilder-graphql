package io.apibuilder.graphql.generators.builders

import io.apibuilder.graphql.util.Text

case class JavaScriptValue(code: String, comment: Option[String] = None)

case class JavaScriptObjectBuilder(name: String, values: Map[String, JavaScriptValue] = Map.empty) {

  def withJavaScript(name: String, javascript: String, comment: Option[String] = None): JavaScriptObjectBuilder = {
    this.copy(
      values = values ++ Map(name -> JavaScriptValue(javascript, comment))
    )
  }

  def withValue(name: String, value: String): JavaScriptObjectBuilder = {
    this.copy(
      values = values ++ Map(name -> JavaScriptValue(Text.wrapInQuotes(value)))
    )
  }

  def build(): String = {
    Seq(
      s"$name: {",
      Text.indent(
        values.zipWithIndex.map { case ((k, v), i) =>
          val c = v.comment match {
            case None => ""
            case Some(comment) => s" // $comment"
          }
          val comma = if (i == values.size - 1) { "" } else { "," }
          s"$k: ${v.code}$comma$c"
        }.mkString("\n")
      ),
      "}",
    ).mkString("\n")
  }
}
