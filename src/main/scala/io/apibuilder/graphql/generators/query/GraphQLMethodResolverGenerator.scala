package io.apibuilder.graphql.generators.query

import apibuilder.{ApiBuilderHelper, ApiBuilderHelperImpl}
import io.apibuilder.graphql.GraphQLOperation
import io.apibuilder.graphql.generators.builders.TypeScriptFileBuilder
import io.apibuilder.graphql.generators.schema.LocalScalarType
import io.apibuilder.graphql.schema.GraphQLIntent
import io.apibuilder.graphql.util.{Constants, Text}
import io.apibuilder.spec.v0.models.{Operation, Parameter, ParameterLocation}
import io.apibuilder.validation.{ApiBuilderService, ApiBuilderType, MultiService, ScalarType}

/**
 * Takes a list of operations and generates the appropriate graphQL query
 * or mutation resolver.
 */
case class GraphQLMethodResolverGenerator(multiService: MultiService) extends ParameterHelpers {

  private[this] val helper:  ApiBuilderHelper = ApiBuilderHelperImpl(multiService)
  private[this] val queryMutationTypeGenerator: GraphQLQueryMutationTypeGenerator = GraphQLQueryMutationTypeGenerator(multiService)

  def generate(builder: TypeScriptFileBuilder, intent: GraphQLIntent): Unit = {
    val all = queryMutationTypeGenerator.generateOperations(intent)
    if (Constants.Resolvers.includeNamespaces(intent)) {
      all.sortBy(_.name.toLowerCase()).foreach { qm =>
        val subBuilder = TypeScriptFileBuilder()
        qm.operations.map(_.operation).foreach { op =>
          generateMethod(subBuilder, op)
        }
        subBuilder.wrapContentWithObject(qm.name)
        subBuilder.build().foreach(builder.add)
      }
    } else {
      all.flatMap(_.operations.map(_.operation)).foreach { op =>
        generateMethod(builder, op)
      }
    }
  }

  /**
   * organization: (_, __, { dataSources }) =>
   *   dataSources.api.get(`/organizations`)
   */
  def generateMethod(builder: TypeScriptFileBuilder, op: GraphQLOperation): Unit = {
    val path = buildPath(op.intentOperation)

    val params = allParametersWithBody(op.intentOperation)
    val (argsName, argsTypes, queryArgsName) = params match {
      case Nil => (", __: any", "", "")
      case params => {
        val body = buildBodyFragment(builder, op)
        val query = buildQueryFragment(op.service, params)
        (wrapInBrackets(", ", toNames(params)), wrapInBrackets(": ", toTypes(params)), s"$body$query")
      }
    }

    builder.addFragment(
      Seq(
        s"${op.attribute.name}: (_: any$argsName$argsTypes, { dataSources }: { dataSources: any }) =>",
        s"  dataSources.api.${op.intentOperation.method.toString.toLowerCase()}($path$queryArgsName),"
      ).mkString("\n")
    )
  }

  private[this] def wrapInBrackets(delim: String, values: Seq[String]): String = s"$delim{ ${values.mkString(", ")} }"

  private[this] def toTypes(parameters: Iterable[Parameter]): List[String] = {
    parameters.map { p =>
      val paramType = LocalScalarType.fromApiBuilderTypeName(p.`type`).map(_.typeScriptType).getOrElse("any")
      s"${Text.camelCase(p.name)}: $paramType"
    }.toList
  }

  private[this] def toNames(parameters: Iterable[Parameter]): List[String] = {
    parameters.map(_.name).map(Text.camelCase).toList
  }

  private[this] def buildQueryFragment(service: ApiBuilderService, params: List[Parameter]): String = {
    params.filter(_.location == ParameterLocation.Query) match {
      case Nil => ""
      case all => {
        wrapInBrackets(
          ", ",
          all.map { p =>
            val graphQLName = Text.camelCase(p.name)
            helper.resolveType(service.service, p) match {
              case Some(t: ApiBuilderType) => {
                s"${p.name}: inputMapper(${Text.wrapInQuotes(Text.pascalCase(t.name))}, ${graphQLName})"
              }
              case Some(_: ScalarType) | None => {
                if (p.name == graphQLName) {
                  graphQLName
                } else {
                  s"${p.name}: ${graphQLName}"
                }
              }
            }
          }
        )
      }
    }
  }

  private[this] def buildBodyFragment(builder: TypeScriptFileBuilder, op: GraphQLOperation): String = {
    GraphQLIntent(op.intentOperation.method) match {
      case GraphQLIntent.Query => ""
      case GraphQLIntent.Mutation => {
        op.intentOperation.body match {
          case None => ", {}"
          case Some(body) => {
            helper.resolveType(op.service, body.`type`) match {
              case None => ", body"
              case Some(bt) => {
                builder.addImport("inputMapper")
                s", inputMapper(${Text.wrapInQuotes(Text.pascalCase(bt.name))}, body)"
              }
            }
          }
        }
      }
    }
  }

  private[this] def buildPath(operation: Operation): String = {
    operation.parameters.filter(_.location == ParameterLocation.Path).toList match {
      case Nil => s""""${operation.path}""""
      case params => {
        val path = params.foldLeft(operation.path.split("/")) { case (all, b) =>
          all.map { w =>
            if (w == s":${b.name}") {
              "${" + Text.camelCase(b.name) + "}"
            } else {
              w
            }
          }
        }.mkString("/")
        s"`$path`"
      }
    }
  }
}
