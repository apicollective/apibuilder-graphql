package io.apibuilder.graphql.generators.schema

import io.apibuilder.graphql.schema.{GraphQLType, GraphQLTypeUnionType}
import io.apibuilder.validation.ApiBuilderType

case class UnionGenerator() {

  def generate(types: ApiBuilderTypeToGraphQLConverter, union: ApiBuilderType.Union): GraphQLType = {
    GraphQLType.Union(
      union,
      union.union.types.map(_.`type`).map( t =>
        GraphQLTypeUnionType(
          types.mustFindFieldTypeDeclaration(s"Union '${union.qualified}' type '$t'", t)
        )
      )
    )
  }

}
