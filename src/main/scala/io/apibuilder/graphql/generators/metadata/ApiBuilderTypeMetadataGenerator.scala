package io.apibuilder.graphql.generators.metadata

import io.apibuilder.graphql.schema.{ApiBuilderTypeMetadata, GraphQLType, GraphQLTypeField}
import io.apibuilder.graphql.util.Text

/**
 * Metadata on models and fields to:
 *   - move back and forth from snake case to server representation
 *   - understand the type of each field
 */
object ApiBuilderTypeMetadataGenerator {

  def generate(types: Seq[GraphQLType]): Option[ApiBuilderTypeMetadata] = {
    types.flatMap(generate).toList match {
      case Nil => None
      case values => Some(
        ApiBuilderTypeMetadata(
          Seq(
            ApiBuilderTypeMetadataConstants.Code,
            Seq(
              "export const models = new Models([",
              Text.indent(values.mkString(",\n")),
              "]);"
            ).mkString("\n")
          ).mkString("\n\n")
        )
      )
    }
  }

  private[this] def generate(typ: GraphQLType): Option[String] = {
    typ match {
      case t: GraphQLType.Type => Some(generate(t.name, t.fields))
      case t: GraphQLType.Input => Some(generate(t.name, t.fields))
      case t: GraphQLType.Enum => Some(generate(t))
      case _: GraphQLType.Array | _: GraphQLType.Union | _: GraphQLType.Scalar | _: GraphQLType.Query | _: GraphQLType.Mutation => None
    }
  }

  private[this] def generate(`enum`: GraphQLType.Enum): String = {
    Seq(
      s"new EnumData(${Text.wrapInQuotes(`enum`.name)}, [",
      Text.indent(
        `enum`.apiBuilderEnum.`enum`.values.map { v =>
          val serverName = v.value.getOrElse(v.name)
          val graphQLName = Text.allCaps(serverName)
          s"new EnumValue(${Text.wrapInQuotes(graphQLName)}, ${Text.wrapInQuotes(serverName)})"
        }.mkString(",\n")
      ),
      "])"
    ).mkString("\n")
  }

  private[this] def generate(name: String, fields: Seq[GraphQLTypeField]): String = {
    Seq(
      s"new ModelData(${Text.wrapInQuotes(name)}, [",
      Text.indent(
        fields.map { f =>
          s"field(${Text.wrapInQuotes(f.name)}, ${Text.wrapInQuotes(f.originalName)}, ${Text.wrapInQuotes(f.typeName)}, ${Text.wrapInQuotes(f.typeKind)})"
        }.mkString(",\n")
      ),
      "])"
    ).mkString("\n")
  }

}
