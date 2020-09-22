package io.apibuilder.graphql.util

import io.apibuilder.graphql.schema.GraphQLType

/**
 * https://www.apollographql.com/docs/apollo-server/schema/schema/#:~:text=Naming%20conventions&text=Field%20names%20should%20use%20camelCase,recommend%20camelCase%20for%20variable%20names.&text=Enum%20names%20should%20use%20PascalCase,they%20are%20similar%20to%20constants.
 *
 * Naming conventions
 * Field names should use camelCase. Many GraphQL clients are written in JavaScript, Java, Kotlin, or Swift, all of which recommend camelCase for variable names.
 * Type names should use PascalCase. This matches how classes are defined in the languages mentioned above.
 * Enum names should use PascalCase.
 * Enum values should use ALL_CAPS, because they are similar to constants.
 */
object Text {

   def stripSuffix(value: String, suffix: String): String = {
    if (value.endsWith(suffix)) {
      value.dropRight(suffix.length)
    } else {
      value
    }
  }

  def wrapInQuotes(str: String): String = {
    "\"" + str.replaceAll("\"", """\\"""") + "\""
  }

  def format(values: Seq[_]): String = {
    values.map {
      case a: Seq[_] => indent(format(a))
      case t: GraphQLType => t.formatted
      case v: String => v
      case other => sys.error(s"Invalid type for format: ${other.getClass.getName}")
    }.mkString("\n")
  }

  def indent(value: String, width: Int = 2): String = {
    value.split("\n").map { value =>
      if (value.trim == "") {
        ""
      } else {
        (" " * width) + value
      }
    }.mkString("\n")
  }

  def allCaps(value: String): String = {
    splitIntoWords(value).mkString("_").toUpperCase()
  }

  def pascalCase(value: String): String = {
    camelCase(value).capitalize
  }

  def camelCase(value: String): String = {
    camelCase(splitIntoWords(value))
  }

  def camelCase(words: Seq[String]): String = {
    words.toList match {
      case Nil => ""
      case part :: rest => part + capitalize(rest)
    }
  }

  private[this] val WordDelimiterRx = "_|\\-|\\.|:|/| ".r

  def splitIntoWords(value: String): Seq[String] = {
    WordDelimiterRx.split(camelCaseToUnderscore(value)).toSeq.map(_.trim).filter(!_.isEmpty)
  }

  private[this] def capitalize(parts: Seq[String]): String = {
    parts.map(_.capitalize).mkString("")
  }

  private[this] val Capitals = """([A-Z])""".r
  private[this] def camelCaseToUnderscore(phrase: String): String = {
    if (phrase == phrase.toUpperCase) {
      phrase.toLowerCase
    } else {
      val word = Capitals.replaceAllIn(phrase, m => s"_${m}").trim
      if (word.startsWith("_")) {
        word.slice(1, word.length)
      } else {
        word
      }
    }
  }
}
