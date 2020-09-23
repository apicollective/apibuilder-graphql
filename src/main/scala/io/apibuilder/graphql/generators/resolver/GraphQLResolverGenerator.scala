package io.apibuilder.graphql.generators.resolver

import io.apibuilder.graphql.generators.builders.{JavaScriptObjectBuilder, TypeScriptFileBuilder}
import io.apibuilder.graphql.generators.query.{GraphQLMethodResolverGenerator, GraphQLQueryMutationTypeGenerator}
import io.apibuilder.graphql.schema.{GraphQLIntent, GraphQLResolvers, GraphQLType}
import io.apibuilder.graphql.util.Constants
import io.apibuilder.validation.{ApiBuilderType, MultiService}

case class GraphQLResolverGenerator(multiService: MultiService) {

  private[this] val unionResolverGenerator: UnionResolverGenerator = UnionResolverGenerator()
  private[this] val enumResolverGenerator: EnumResolverGenerator = EnumResolverGenerator()
  private[this] val queryMutationTypeGenerator: GraphQLQueryMutationTypeGenerator = GraphQLQueryMutationTypeGenerator(multiService)
  private[this] val methodResolverGenerator: GraphQLMethodResolverGenerator = GraphQLMethodResolverGenerator(multiService)
  private[this] val MagicDelegation = "() => ({})" // allows to proceed call of sub-methods

  def generate(types: Seq[GraphQLType], intent: GraphQLIntent): Option[GraphQLResolvers] = {
    val builder = TypeScriptFileBuilder()
    builder.setWrapWithDefaultExport(true)

    val all = queryMutationTypeGenerator.generateOperations(intent)
    if (Constants.Resolvers.includeNamespaces(intent)) {
      all.sortBy(_.name.toLowerCase()).foreach { qm =>
        val subBuilder = TypeScriptFileBuilder()
        qm.operations.sortBy(_.operation.resource.`type`.name).foreach { op =>
          methodResolverGenerator.generateMethod(subBuilder, op.operation)
        }
        subBuilder.wrapContentWithObject(qm.subTypeName)
        subBuilder.build().foreach(builder.add)
      }

      val typeBuilder = all.foldLeft(JavaScriptObjectBuilder(toTypeName(intent))) { case (b, qm) =>
        b.withJavaScript(qm.name, MagicDelegation, Some(qm.subTypeName))
      }

      if (builder.nonEmpty) {
        builder.addFragment(typeBuilder.build())
      }
    } else {
      all.flatMap(_.operations).sortBy(_.operation.resource.`type`.name).foreach { op =>
        methodResolverGenerator.generateMethod(builder, op.operation)
      }
      if (builder.nonEmpty) {
        builder.wrapContentWithObject(toTypeName(intent))
      }
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
