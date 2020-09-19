package io.apibuilder.graphql.util

import io.apibuilder.graphql.GraphQLOperation
import io.apibuilder.graphql.schema.GraphQLIntent
import io.apibuilder.rewriter.{MinimalTypesRewriter, MultiServiceRewriter}
import io.apibuilder.validation.MultiService

/**
 * Collect the types references by the GraphQL Operations and reduce set of types to
 * only those referenced from them
 */
case class ReduceTypesRewriter(intent: GraphQLIntent) extends MultiServiceRewriter {
  override def rewrite(multiService: MultiService): MultiService = {
    val operationTypes = GraphQLOperation.all(multiService, intent).flatMap(_.originalTypes)
    MinimalTypesRewriter(operationTypes).rewrite(multiService)
  }
}
