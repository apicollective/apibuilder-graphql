package io.apibuilder.graphql.generators.resolver

import io.apibuilder.graphql.util.Text
import io.apibuilder.validation.ApiBuilderType

case class UnionResolverGenerator() extends ResolverHelpers {

  // TODO: Should we encapsulate formatting along with union generator class?
  def generate(union: ApiBuilderType.Union): String = {
    val discriminatorName = union.union.discriminator.getOrElse {
      sys.error(s"Union ${union.qualified} must have a discriminator")
    }

    generateResolver(union)(
      Seq(
        "__resolveType(obj: any, _: any, __: any) {",
        s"  switch (obj.$discriminatorName) {",
        Text.indent(generateTypes(union), 4),
        "  }",
        Text.indent(generateDefault(union, discriminatorName)),
        "}",
      ).mkString("\n")
    )
  }

  private[this] def generateTypes(union: ApiBuilderType.Union): String = {
    union.union.types.flatMap { t =>
      (Seq(t.`type`) ++ t.discriminatorValue.toSeq).distinct.map { wireValue =>
        s"""case "$wireValue":\n  return "${Text.pascalCase(t.`type`)}";"""
      }
    }.mkString("\n")
  }

  private[this] def generateDefault(union: ApiBuilderType.Union, discriminatorName: String) = {
    union.union.types.find(_.default.getOrElse(false)) match {
      case None => s"throw `Unable to resolve ${discriminatorName} '" + "$" + s"{obj.discriminator}' for union '${name(union)}'`;"
      case Some(t) => s"return ${Text.wrapInQuotes(Text.pascalCase(t.`type`))};"
    }
  }
}
