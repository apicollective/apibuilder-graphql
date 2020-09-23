package io.apibuilder.graphql.generators.builders

import io.apibuilder.graphql.util.Text

case class JavaScriptObjectBuilder(name: String, values: Map[String, String] = Map.empty) {

  def withJavaScript(name: String, javascript: String): JavaScriptObjectBuilder = {
    this.copy(
      values = values ++ Map(name -> javascript)
    )
  }

  def withValue(name: String, value: String): JavaScriptObjectBuilder = {
    withValues(Map(name -> value))
  }

  def withValues(all: Map[String, String]): JavaScriptObjectBuilder = {
    this.copy(
      values = values ++ all.map { case (k, v) => k -> Text.wrapInQuotes(v) }
    )
  }

  def build(): String = {
    Seq(
      s"$name: {",
      Text.indent(
        values.map { case (k, v) => s"$k: $v" }.mkString(",\n")
      ),
      "}",
    ).mkString("\n")
  }
}
