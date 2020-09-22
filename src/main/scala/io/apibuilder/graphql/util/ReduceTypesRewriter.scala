package io.apibuilder.graphql.util

import io.apibuilder.graphql.GraphQLOperation
import io.apibuilder.graphql.schema.GraphQLIntent
import io.apibuilder.rewriter.{MinimalTypesRewriter, MultiServiceRewriter}
import io.apibuilder.validation.{ApiBuilderType, MultiService}

/**
 * Collect the types references by the GraphQL Operations and reduce set of types to
 * only those referenced from them
 */
case class ReduceTypesRewriter(intent: GraphQLIntent) extends MultiServiceRewriter {
  override def rewrite(multiService: MultiService): MultiService = {
    val all = GraphQLOperation.all(multiService, intent)
    val operationTypes = all.flatMap(_.originalTypes) ++
      all.map(_.resource.`type`).collect { case t: ApiBuilderType => t }
    MinimalTypesRewriter(operationTypes).rewrite(multiService)
  }
}
