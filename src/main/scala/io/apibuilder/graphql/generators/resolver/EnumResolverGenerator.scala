package io.apibuilder.graphql.generators.resolver

import io.apibuilder.graphql.util.Text
import io.apibuilder.validation.ApiBuilderType

case class EnumResolverGenerator() extends ResolverHelpers {

  def generate(enum: ApiBuilderType.Enum): String = {
    // TODO: Should we encapsulate formatting along with enum generator class?
    generateResolver(enum)(
      enum.`enum`.values.map { v =>
        val wireValue = v.value.getOrElse(v.name)
        val graphQLValue = Text.allCaps(wireValue)
        s"$graphQLValue: " + Text.wrapInQuotes(wireValue)
      }.mkString(",\n")
    )
  }

}
