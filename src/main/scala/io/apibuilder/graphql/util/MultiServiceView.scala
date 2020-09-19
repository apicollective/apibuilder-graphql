package io.apibuilder.graphql.util

import apibuilder.ApiBuilderHelper
import io.apibuilder.rewriter._
import io.apibuilder.graphql.schema.GraphQLIntent
import io.apibuilder.graphql.GraphQLAttribute
import io.apibuilder.spec.v0.models.{Operation, Response}
import io.apibuilder.validation.MultiService

object MultiServiceView {
  val InputSuffix: String = "input"
}

/**
 * Modifies a Multi Service to provide consistent types as required by GraphQL:
 *   - Remove union types that are not models
 *   - Append an _input suffix to all types used in mutations
 *   - Rewrite mutation union types as models
 */
case class MultiServiceView(multiService: MultiService) {

  lazy val query: MultiService = rewrite(GraphQLIntent.Query)

  lazy val mutation: MultiService = rewrite(GraphQLIntent.Mutation)

   private[this] def rewrite(intent: GraphQLIntent): MultiService = {
    rewriters(intent).foldLeft(multiService) { case (ms, rewriter) =>
      println(s"${intent}: Starting rewriter: ${rewriter.getClass.getName}")
      rewriter.rewrite(ms)
    }
  }

  private[this] def rewriters(intent: GraphQLIntent): List[MultiServiceRewriter] = {
    val common = List(
      FilterOperationsRewriter { op =>
        GraphQLAttribute.fromOperation(op).map(_ => op)
      },
      FilterResponsesRewriter { operation =>
        val all = operation.responses.filter(ApiBuilderHelper.is2xx)
        assertSingleResponseType(operation, all)
        all.headOption.toSeq
      },
      UnionTypesMustBeModelsRewriter,
      RenameTypesByAttributeRewriter(intent),
      ReduceTypesRewriter(intent),
    )

    intent match {
      case GraphQLIntent.Query => common
      case GraphQLIntent.Mutation => common ++ List(
        UnionsToModelsRewriter,
        AppendInputSuffixRewriter
      )
    }
  }

  private[this] def assertSingleResponseType(operation: Operation, responses: Seq[Response]): Unit = {
    val responseTypes = responses.map(_.`type`).distinct
    if (responseTypes.size > 1) {
      sys.error(
        s"Multiple 2xx response types for operation: '${operation.path}': ${responseTypes.mkString(", ")}'"
      )
    }
  }
}
