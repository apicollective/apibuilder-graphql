package io.apibuilder.graphql.util

import io.apibuilder.graphql.generators.helpers.GraphQLApiBuilderServiceHelpers
import io.apibuilder.graphql.generators.schema.ApiBuilderTypeToGraphQLConverter
import io.apibuilder.graphql.schema.{GraphQLIntent, GraphQLType, NamedGraphQLType}
import io.apibuilder.builders.MultiServiceBuilders
import io.apibuilder.validation.MultiService
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MultiServiceViewSpec extends AnyWordSpec with Matchers
  with MultiServiceBuilders
  with GraphQLApiBuilderServiceHelpers
{

  "mutation enums do not have an _input suffix" in {
    val enum = makeValidEnum("environment")
    val ms = makeMultiService(
      makeService(
        enums = Seq(enum),
        resources = Seq(
          makeResource(
            operations = Seq(
              makeOperation(
                responses = Seq(make200Response("environment")),
                attributes = Seq(makeGraphQLAttribute("create"))
              )
            )
          )
        )
      )
    )

    def buildTypeSchema(ms: MultiService, intent: GraphQLIntent): Seq[GraphQLType] = {
      ms.allTypes.map { t =>
        val converter = ApiBuilderTypeToGraphQLConverter(ms, intent, namespace = t.namespace)
        converter.convert(t)
      }
    }

    val view = MultiServiceView(ms)
    mustFindEnum(view.mutation, "environment")
    val types = buildTypeSchema(view.mutation, GraphQLIntent.Mutation)
    types.foreach {
      case t: NamedGraphQLType => println(s" - t: ${t.name}")
      case _ => //
    }
    def findType(name: String): Option[NamedGraphQLType] = {
      types.collectFirst { case t: NamedGraphQLType if t.name == name => t }
    }
    findType("Environment").getOrElse {
      sys.error("Missing Environment")
    }
  }

}
