package io.apibuilder.graphql.generators.resolver

import io.apibuilder.graphql.util.Text
import io.apibuilder.validation.ApiBuilderType

trait ResolverHelpers {
  def name(typ: ApiBuilderType): String = Text.pascalCase(typ.name)

  def generateResolver(typ: ApiBuilderType)(data: String): String = {
    Seq(
      s"${name(typ)}: {",
      Text.indent(data),
      "}",
    ).mkString("\n")
  }
}
