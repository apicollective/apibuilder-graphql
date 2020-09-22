package io.apibuilder.graphql.generators.resolver

import io.apibuilder.graphql.generators.builders.TypeScriptFileBuilder
import io.apibuilder.graphql.generators.query.GraphQLMethodResolverGenerator
import io.apibuilder.graphql.schema.{GraphQLIntent, GraphQLResolvers, GraphQLType}
import io.apibuilder.validation.{ApiBuilderType, MultiService}

case class GraphQLResolverGenerator(multiService: MultiService) {

  private[this] val unionResolverGenerator: UnionResolverGenerator = UnionResolverGenerator()
  private[this] val enumResolverGenerator: EnumResolverGenerator = EnumResolverGenerator()
  private[this] val methodResolverGenerator: GraphQLMethodResolverGenerator = GraphQLMethodResolverGenerator(multiService)

  def generate(types: Seq[GraphQLType], intent: GraphQLIntent): Option[GraphQLResolvers] = {
    val builder = TypeScriptFileBuilder()
    builder.setWrapWithDefaultExport(true)

    methodResolverGenerator.generate(builder, intent)
    if (builder.nonEmpty) {
      builder.wrapContentWithObject(toTypeName(intent))
    }
    generateEnums(builder, types.collect { case t: GraphQLType.Enum => t.apiBuilderEnum})
    generateUnions(builder, types.collect { case t: GraphQLType.Union => t.apiBuilderUnion})

    builder.build().map(GraphQLResolvers)
  }

  private[this] def toTypeName(intent: GraphQLIntent): String = {
    intent match {
      case GraphQLIntent.Query => "Query"
      case GraphQLIntent.Mutation => "Mutation"
    }
  }

  private[this] def generateEnums(builder: TypeScriptFileBuilder, enums: Seq[ApiBuilderType.Enum]): Unit = {
    enums.foreach { e =>
      builder.addFragment(enumResolverGenerator.generate(e))
    }
  }

  private[this] def generateUnions(builder: TypeScriptFileBuilder, unions: Seq[ApiBuilderType.Union]): Unit = {
    unions.foreach { u =>
      builder.addFragment(unionResolverGenerator.generate(u))
    }
  }
}
