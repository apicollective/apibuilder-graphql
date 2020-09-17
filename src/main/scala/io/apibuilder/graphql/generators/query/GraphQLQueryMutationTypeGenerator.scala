package io.apibuilder.graphql.generators.query

import apibuilder.ApiBuilderHelperImpl
import io.apibuilder.graphql.GraphQLOperation
import io.apibuilder.graphql.generators.schema.ApiBuilderTypeToGraphQLConverter
import io.apibuilder.graphql.schema.{GraphQLIntent, GraphQLQueryMutationType, GraphQLType}
import io.apibuilder.graphql.util.{MultiServiceView, Text}
import io.apibuilder.spec.v0.models._
import io.apibuilder.validation.{ApiBuilderService, ApiBuilderType, MultiService, ScalarType}

case class GraphQLQueryMutation(
  intent: GraphQLIntent,
  resourceType: ApiBuilderType,
  code: String,
) {
  private[this] val suffix: String = intent match {
    case GraphQLIntent.Query => "Queries"
    case GraphQLIntent.Mutation => "Mutations"
  }

  val name: String = Text.pascalCase(resourceType.name)
  val subTypeName: String = s"$name$suffix"
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

  private[this] def generateOperations(intent: GraphQLIntent): Seq[GraphQLQueryMutation] = {
    GraphQLOperation.all(multiService, intent)
      .filter(_.methodIntent == intent)
      .groupBy(_.resource.`type`).map { case (resourceType, resourceOperations) =>
      GraphQLQueryMutation(
        intent,
        resourceType,
        resourceOperations.map(generateOperations).mkString("\n"),
      )
    }.toSeq
  }

  private[this] def generateOperations(op: GraphQLOperation): String = {
    val converter = ApiBuilderTypeToGraphQLConverter(
      multiService,
      op.graphQLIntent,
      namespace = op.service.namespace,
    )

    val params = allParametersWithBody(op.originalOperation).map { p => toParam(converter, op, p) } match {
      case Nil => ""
      case els => "(" + els.mkString(", ") + ")"
    }
    val responseType = stripInputSuffix(converter.mustFindFieldTypeDeclaration(op.description, op.response.`type`))

    Seq(
      toComment(op),
      s"${op.attribute.name}$params: $responseType"
    ).mkString("\n")
  }

  private[this] def toComment(op: GraphQLOperation): String = {
    Seq(
      s"Resource ${op.resource.`type`.qualified} ${op.resource.resource.path.getOrElse("N/A")}",
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

  private[this] def stripInputSuffix(value: String): String = {
    val input = Text.pascalCase(MultiServiceView.InputSuffix)
    if (value.endsWith(input)) {
      value.dropRight(input.length)
    } else {
      value
    }
  }
}
