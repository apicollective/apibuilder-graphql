package io.apibuilder.graphql.generators.helpers

import io.apibuilder.graphql.generators.query.GraphQLQueryMutationTypeGenerator
import io.apibuilder.graphql.schema.GraphQLIntent
import io.apibuilder.spec.v0.models.Service
import io.apibuilder.validation.MultiService

/**
 * Utility to compare a string to an expected value stored in a file
 */
trait QueryMutationHelpers extends GraphQLApiBuilderServiceHelpers with CodeGenTestHelpers {

  def verify(fileName: String, service: Service, intent: GraphQLIntent): Unit = {
    verify(fileName: String, makeMultiService(service), intent)
  }

  def verify(fileName: String, multiService: MultiService, intent: GraphQLIntent): Unit = {
    mustMatchFile(
      fileName,
      GraphQLQueryMutationTypeGenerator(multiService).generate(intent).get.formatted
    )
  }

}


