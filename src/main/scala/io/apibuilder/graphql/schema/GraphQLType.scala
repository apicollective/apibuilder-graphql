package io.apibuilder.graphql.schema

import io.apibuilder.graphql.generators.query.GraphQLQueryMutation
import io.apibuilder.graphql.generators.schema.LocalScalarType
import io.apibuilder.graphql.util.{Constants, MultiServiceView, Text}
import io.apibuilder.spec.v0.models.Method
import io.apibuilder.validation.ApiBuilderType

import scala.annotation.tailrec

sealed trait GraphQLIntent
object GraphQLIntent {
  case object Mutation extends GraphQLIntent
  case object Query extends GraphQLIntent

  def apply(method: Method): GraphQLIntent = {
    import Method._
    method match {
      case Get | Connect | Head | Options | Trace | UNDEFINED(_) => GraphQLIntent.Query
      case Delete | Post | Put | Patch => GraphQLIntent.Mutation
    }
  }
}

sealed trait GraphQLType {
  def formatted: String
}

sealed trait NamedGraphQLType extends GraphQLType {
  def originalName: String
  def name: String
}

sealed trait GraphQLQueryMutationType extends NamedGraphQLType {
  def operations: Seq[GraphQLQueryMutation]
}

case class GraphQLTypeUnionType(originalName: String) {
  val name: String = Text.pascalCase(originalName)
}

case class GraphQLTypeField(
  originalName: String,
  declaration: String,
  required: Boolean,
  `type`: GraphQLType,
) {
  private[this] val requiredFlag: String = if (required) {
    "!"
  } else {
    ""
  }

  val name: String = Text.camelCase(originalName)
  val formatted: String = s"$name: ${declaration}$requiredFlag"

  val typeName: String = toName(`type`)
  val typeKind: String = toKind(`type`)

  @tailrec
  private[this] def toName(graphQLType: GraphQLType): String = {
    graphQLType match {
      case n: NamedGraphQLType => n.name
      case a: GraphQLType.Array => toName(a.`type`)
    }
  }

  private[this] def toKind(graphQLType: GraphQLType): String = {
    graphQLType match {
      case _: GraphQLType.Scalar => "scalar"
      case _: GraphQLType.Union => "union"
      case _: GraphQLType.Enum => "enum"
      case _: GraphQLType.Type => "type"
      case _: GraphQLType.Input => "input"
      case _: GraphQLType.Array => "array"
      case _: GraphQLType.Query => "query"
      case _: GraphQLType.Mutation => "mutation"
    }
  }

}

object GraphQLType {
  case class Scalar(scalar: LocalScalarType) extends NamedGraphQLType {
    override val originalName: String = scalar.apiBuilderType.name
    override val name: String = scalar.graphQLType
    override val formatted: String = s"scalar ${name}"
  }

  case class Enum(apiBuilderEnum: ApiBuilderType.Enum)
    extends StaticType(
      "enum",
      apiBuilderEnum.name,
      apiBuilderEnum.enum.values.map { v => Text.allCaps(v.value.getOrElse(v.name)) },
      comment = Some(toComment(apiBuilderEnum)),
    ) with NamedGraphQLType {
    assert(!name.endsWith("Input"), "Enum names should not have Input appended")
  }

  case class Union(apiBuilderUnion: ApiBuilderType.Union, types: Seq[GraphQLTypeUnionType]) extends NamedGraphQLType {
    override val originalName: String = apiBuilderUnion.name
    override val name: String = Text.pascalCase(originalName)
    override val formatted: String = Seq(
      s"# ${toComment(apiBuilderUnion)}",
      s"union $name = " + types.map(_.name).mkString(" | ")
    ).mkString("\n")
  }

  object Type {
    def apply(apiBuilderType: ApiBuilderType, fields: Seq[GraphQLTypeField]): Type = {
      Type(
        apiBuilderType.name,
        fields = fields,
        comment = Some(toComment(apiBuilderType)),
      )
    }
  }

  case class Query(operations: Seq[GraphQLQueryMutation]) extends QueryMutationType(GraphQLIntent.Query, "query", operations) with GraphQLQueryMutationType

  case class Mutation(operations: Seq[GraphQLQueryMutation]) extends QueryMutationType(GraphQLIntent.Mutation, "mutation", operations) with GraphQLQueryMutationType

  case class Type(override val originalName: String, fields: Seq[GraphQLTypeField], comment: Option[String])
    extends StaticType("type", originalName, fields.map(_.formatted), comment)
    with NamedGraphQLType

  object Input {
    def apply(apiBuilderType: ApiBuilderType, fields: Seq[GraphQLTypeField]): Input = {
      Input(apiBuilderType.name, fields, Some(toComment(apiBuilderType)))
    }
  }

  private[this] def toComment(apiBuilderType: ApiBuilderType) = {
    val all = apiBuilderType.qualified.split("_")
    if (all.lastOption.contains(MultiServiceView.InputSuffix)) {
      val n = all.dropRight(1).mkString("_")
      s"added automatically from $n"
    } else {
      apiBuilderType.qualified
    }
  }

  case class Input(override val originalName: String, fields: Seq[GraphQLTypeField], comment: Option[String])
    extends StaticType("input", originalName, fields.map(_.formatted), comment)
    with NamedGraphQLType {
    assert(originalName.endsWith("_input"), s"Input name '$originalName' must end with '_input'")
  }

  case class Array(`type`: GraphQLType) extends GraphQLType {
    override val formatted: String = "[" + `type`.formatted + "!]"
  }

  abstract class StaticType(loc: String, val originalName: String, values: Seq[String], comment: Option[String]) {
    val name: String = Text.pascalCase(originalName)
    val formatted: String = (comment.toSeq.map { c => s"# $c" } ++ Seq(
      s"$loc $name {",
      Text.indent(Text.format(values)),
      "}",
    )).mkString("\n")
  }

  abstract class QueryMutationType(intent: GraphQLIntent, val originalName: String, operations: Seq[GraphQLQueryMutation]) {
    private[this] val sortedOperations = operations.sortBy(_.name.toLowerCase())
    val name: String = Text.pascalCase(originalName)
    val formatted: String = {
      if (Constants.Resolvers.includeNamespaces(intent)) {
        namespaces
      } else {
        flat
      }
    }

    private[this] def flat: String = makeType(name, sortedOperations.flatMap(_.operations.map(_.code)))

    private[this] def namespaces: String = (
      Seq(
        makeType(name, sortedOperations.map { op =>
          s"${op.name}: ${op.subTypeName}"
        })
      ) ++ sortedOperations.map { op =>
        makeType(op.subTypeName, op.operations.map(_.code))
      }
    ).mkString("\n\n")
  }

  private[this] def makeType(name: String, values: Seq[String]): String = {
    Seq(
      s"type ${Text.pascalCase(name)} {",
      Text.indent(values.mkString("\n\n")),
      "}",
    ).mkString("\n")
  }
}
