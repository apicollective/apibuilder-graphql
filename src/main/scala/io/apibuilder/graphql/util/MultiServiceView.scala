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

  private[this] def assertSingleResponseType(operation: Operation, responses: Seq[Response]): Unit = {
    val responseTypes = responses.map(_.`type`).distinct
    if (responseTypes.size > 1) {
      sys.error(
        s"Multiple 2xx response types for operation: '${operation.path}': ${responseTypes.mkString(", ")}'"
      )
    }
  }

  private[this] val base: MultiService = {
    val ms1 = FilterOperationsRewriter { op =>
      GraphQLAttribute.fromOperation(op).map(_ => op)
    }.rewrite(multiService)

    val ms2 = FilterResponsesRewriter{ operation =>
      val all = operation.responses.filter(ApiBuilderHelper.is2xx)
      assertSingleResponseType(operation, all)
      all.headOption.toSeq
    }.rewrite(ms1)

    UnionTypesMustBeModelsRewriter.rewrite(ms2)
  }

  lazy val query: MultiService = GraphQLIntentRewriter.rewrite(base, GraphQLIntent.Query)

  lazy val mutation: MultiService = {
    val ms1 = GraphQLIntentRewriter.rewrite(base, GraphQLIntent.Mutation)
    val ms2 = UnionsToModelsRewriter.rewrite(ms1)
    AppendInputSuffixRewriter.rewrite(ms2)
  }

}
