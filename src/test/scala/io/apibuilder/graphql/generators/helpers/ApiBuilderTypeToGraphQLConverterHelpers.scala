package io.apibuilder.graphql.generators.helpers

import io.apibuilder.graphql.generators.schema
import io.apibuilder.graphql.generators.schema.ApiBuilderTypeToGraphQLConverter
import io.apibuilder.graphql.schema.GraphQLIntent
import io.apibuilder.builders.MultiServiceBuilders
import io.apibuilder.validation.MultiService

trait ApiBuilderTypeToGraphQLConverterHelpers extends MultiServiceBuilders {

  def makeConverter(
    multiService: MultiService = makeMultiService(),
  ): ApiBuilderTypeToGraphQLConverter = {
    schema.ApiBuilderTypeToGraphQLConverter(
      multiService,
      GraphQLIntent.Query,
      namespace = multiService.services().head.namespace,
    )
  }

}
