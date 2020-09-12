package io.apibuilder.graphql

import cats.data.ValidatedNec
import cats.implicits._
import io.apibuilder.graphql.generators.metadata.ApiBuilderTypeMetadataGenerator
import io.apibuilder.graphql.generators.query.GraphQLQueryMutationTypeGenerator
import io.apibuilder.graphql.generators.resolver.GraphQLResolverGenerator
import io.apibuilder.graphql.schema.{GraphQLGeneratedCode, GraphQLIntent, GraphQLResolvers, GraphQLType, GraphQLTypeSchema, NamedGraphQLType, SchemaValidator}
import io.apibuilder.graphql.generators.schema.{ApiBuilderTypeToGraphQLConverter, LocalScalarType}
import io.apibuilder.graphql.util.MultiServiceView
import io.apibuilder.validation.MultiService

case class GraphQLCodeGenerator(schemaValidator: SchemaValidator) {
  private[this] def buildTypeSchema(ms: MultiService, intent: GraphQLIntent): Seq[GraphQLType] = {
    ms.allTypes.map { t =>
      val converter = ApiBuilderTypeToGraphQLConverter(ms, intent, namespace = t.namespace)
      converter.convert(t)
    }
  }

  private[this] def qmType(multiService: MultiService, intent: GraphQLIntent): Option[Seq[String]] = {
    GraphQLQueryMutationTypeGenerator(multiService).generate(intent).map(Seq(_))
  }

  private[graphql] lazy val schemaScalars: Seq[GraphQLType] = {
    LocalScalarType.all
      .filterNot { t => LocalScalarType.NativeGraphQLTypes.contains(t.graphQLType) }
      .map(GraphQLType.Scalar)
      .sortBy(_.formatted)
  }

  private[this] def generateResolvers(multiService: MultiService, types: Seq[GraphQLType], intent: GraphQLIntent): Option[GraphQLResolvers] = {
    GraphQLResolverGenerator(multiService).generate(types, intent)
  }

  def generate(multiService: MultiService): ValidatedNec[String, GraphQLGeneratedCode] = {
    val view = MultiServiceView(multiService)

    (
      schemaValidator.validate(buildTypeSchema(view.query, GraphQLIntent.Query)),
      schemaValidator.validate(buildTypeSchema(view.mutation, GraphQLIntent.Mutation))
    ).mapN { case (typeSchema, inputSchema) =>
      val finalInputSchema = removeDuplicateTypes(typeSchema, inputSchema)

      GraphQLGeneratedCode(
        typeSchema = typeSchema,
        inputSchema = finalInputSchema,
        scalarSchema = GraphQLTypeSchema(schemaScalars),
        querySchema = qmType(view.query, GraphQLIntent.Query).map(GraphQLType.Query),
        queryResolvers = generateResolvers(view.query, typeSchema.types, GraphQLIntent.Query),
        mutationSchema = qmType(view.mutation, GraphQLIntent.Mutation).map(GraphQLType.Mutation),
        mutationResolvers = generateResolvers(view.mutation, finalInputSchema.types, GraphQLIntent.Mutation),
        typeMetadata = ApiBuilderTypeMetadataGenerator.generate(
          typeSchema.types ++ finalInputSchema.types
        )
      )
    }
  }

  private[this] def removeDuplicateTypes(a: GraphQLTypeSchema, b: GraphQLTypeSchema): GraphQLTypeSchema = {
    val aTypes = a.types.collect { case t: NamedGraphQLType => t.name }.toSet

    GraphQLTypeSchema(
      b.types.flatMap {
        case t: NamedGraphQLType if aTypes.contains(t.name) => None
        case t => Some(t)
      }
    )
  }

}
