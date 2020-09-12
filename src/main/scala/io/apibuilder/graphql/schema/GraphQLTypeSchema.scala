package io.apibuilder.graphql.schema

import io.apibuilder.graphql.generators.builders.TypeScriptFile
import io.apibuilder.generator.v0.models.{File, Invocation}

case class GraphQLGeneratedCode(
  typeSchema: GraphQLTypeSchema,
  inputSchema: GraphQLTypeSchema,
  scalarSchema: GraphQLTypeSchema,
  typeMetadata: Option[ApiBuilderTypeMetadata],
  querySchema: Option[GraphQLType.Query],
  queryResolvers: Option[GraphQLResolvers],
  mutationSchema: Option[GraphQLType.Mutation],
  mutationResolvers: Option[GraphQLResolvers],
) {

  lazy val invocation: Invocation = {
    Invocation(
      source = "See files",
      files = Seq(
        File(name = "schema.graphql", contents = allTypes),
        File("resolvers.ts", contents = allResolvers.map(_.formatted).getOrElse("// No resolvers")),
        File("type-metadata.ts", contents = typeMetadata.map(_.formatted).getOrElse("// No metadata")),
      )
    )
  }

  private[this] val allTypes: String = (
    Seq(
      querySchema.map(_.formatted),
      mutationSchema.map(_.formatted),
    ).flatten ++ Seq(
      scalarSchema.formatted,
      typeSchema.formatted,
      inputSchema.formatted,
    )
  ).mkString("\n\n")

  private[this] val allResolvers: Option[GraphQLResolvers] = {
    (queryResolvers.toSeq ++ mutationResolvers.toSeq).map(_.file).toList match {
      case Nil => None
      case one :: rest => Some(GraphQLResolvers(
        rest.foldLeft(one) { case (r, all) => all.merge(r) }
      ))
    }
  }
}

case class GraphQLTypeSchema(types: Seq[GraphQLType]) {
  val formatted: String = types.map(_.formatted).mkString("\n\n")
}

case class GraphQLResolvers(file: TypeScriptFile) {
  lazy val formatted: String = file.formatted
}

case class ApiBuilderTypeMetadata(formatted: String)
