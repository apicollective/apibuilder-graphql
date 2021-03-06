package io.apibuilder.graphql.generators.query

import apibuilder.ApiBuilderHelperImpl
import io.apibuilder.graphql.GraphQLOperation
import io.apibuilder.graphql.generators.schema.ApiBuilderTypeToGraphQLConverter
import io.apibuilder.graphql.schema.{GraphQLIntent, GraphQLQueryMutationType, GraphQLType}
import io.apibuilder.graphql.util.{MultiServiceView, Text}
import io.apibuilder.spec.v0.models._
import io.apibuilder.validation.{ApiBuilderService, ApiBuilderType, MultiService, ScalarType}

case class GraphQLQueryMutationOperation(
  operation: GraphQLOperation,
  code: String,
)

case class GraphQLQueryMutation(
  intent: GraphQLIntent,
  namespace: String,
  operations: Seq[GraphQLQueryMutationOperation],
) {
  private[this] val suffix: String = intent match {
    case GraphQLIntent.Query => "Queries"
    case GraphQLIntent.Mutation => "Mutations"
  }

  val name: String = Text.camelCase(namespace)
  val subTypeName: String = Text.pascalCase(s"$name$suffix")
}

case class GraphQLQueryMutationTypeGenerator(multiService: MultiService) extends ParameterHelpers {

  private[this] val helper = ApiBuilderHelperImpl(multiService)

  def generate(intent: GraphQLIntent): Option[GraphQLQueryMutationType] = {
    generateOperations(intent).toList match {
      case Nil => None
      case ops => Some(
        intent match {
          case GraphQLIntent.Query => GraphQLType.Query(ops)
          case GraphQLIntent.Mutation => GraphQLType.Mutation(ops)
        }
      )
    }
  }

  def generateOperations(intent: GraphQLIntent): Seq[GraphQLQueryMutation] = {
    GraphQLOperation.all(multiService, intent)
      .filter(_.methodIntent == intent)
      .groupBy(_.namespace).map { case (namespace, resourceOperations) =>
      GraphQLQueryMutation(
        intent,
        namespace,
        resourceOperations.map(generateOperations),
      )
    }.toSeq.sortBy(_.name.toLowerCase())
  }

  private[this] def generateOperations(op: GraphQLOperation): GraphQLQueryMutationOperation = {
    val converter = ApiBuilderTypeToGraphQLConverter(
      multiService,
      op.graphQLIntent,
      namespace = op.service.namespace,
    )

    val params = allParametersWithBody(op.originalOperation).map { p => toParam(converter, op, p) } match {
      case Nil => ""
      case els => "(" + els.mkString(", ") + ")"
    }
    val responseType = MultiServiceView.stripInputSuffix(converter.mustFindFieldTypeDeclaration(op.description, op.response.`type`))

    val code = Seq(
      toComment(op),
      s"${op.attribute.name}$params: $responseType"
    ).mkString("\n")

    GraphQLQueryMutationOperation(op, code)
  }

  private[this] def toComment(op: GraphQLOperation): String = {
    val name = op.resource.`type` match {
      case t: ApiBuilderType => s"Resource ${t.qualified}"
      case t: ScalarType => s"Scalar ${t.name}"
    }
    Seq(
      name,
      op.description,
      s"Response ${toText(op.response.code)} ${op.response.`type`}",
    ).map { p => s"# $p" }.mkString("\n")
  }

  private[this] def toText(code: ResponseCode): String = {
    code match {
      case v: ResponseCodeInt => v.value.toString
      case ResponseCodeUndefinedType(other) => other
      case ResponseCodeOption.Default => "*"
      case ResponseCodeOption.UNDEFINED(other) => other
    }
  }

  private[this] def toParam(converter: ApiBuilderTypeToGraphQLConverter, op: GraphQLOperation, param: Parameter): String = {
    Text.camelCase(param.name) + ": " +
      converter.mustFindFieldTypeDeclaration(s"${op.description} parameter '${param.name}'", param.`type`) +
      requiredFlag(param) +
      defaultValue(op.service, param)
  }

  private[this] def defaultValue(service: ApiBuilderService, param: Parameter): String = {
    param.default match {
      case None => ""
      case Some(d) => {
        def unsupportedError = sys.error(s"Non-scalar type [${param.`type`}] is not supported.")
        import ScalarType._

        val value = helper.resolveType(service.service, param) match  {
          case None => {
            unsupportedError
          }
          case Some(typ) => {
            typ match {
              case s: ScalarType => {
                s match {
                  case BooleanType | DoubleType | IntegerType | DecimalType | FloatType | LongType | JsonType | ObjectType => d
                  case StringType | DateIso8601Type | DateTimeIso8601Type | UuidType => Text.wrapInQuotes(d)
                  case UnitType => "null"
                }
              }
              case _: ApiBuilderType.Model => unsupportedError
              case _: ApiBuilderType.Union => unsupportedError
              case _: ApiBuilderType.Enum => Text.allCaps(d)
            }
          }
        }
        " = " + value
      }
    }
  }

  private[this] def requiredFlag(param: Parameter): String = {
    if (param.required) {
      "!"
    } else {
      ""
    }
  }

}
