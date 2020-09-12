package io.apibuilder.graphql.generators.schema

import io.apibuilder.graphql.schema.{GraphQLIntent, GraphQLType, GraphQLTypeField}
import io.apibuilder.validation.{ApiBuilderField, ApiBuilderType, ScalarType}

case class ModelGenerator() {

  def generate(converter: ApiBuilderTypeToGraphQLConverter, model: ApiBuilderType.Model): GraphQLType = {
    val fields = model.fields.map { f => generateField(converter, f) }
    converter.intent match {
      case GraphQLIntent.Query => GraphQLType.Type(model, fields)
      case GraphQLIntent.Mutation => GraphQLType.Input(model, fields)
    }
  }

  private[this] def generateField(converter: ApiBuilderTypeToGraphQLConverter, field: ApiBuilderField): GraphQLTypeField = {
    GraphQLTypeField(
      originalName = field.field.name,
      declaration = fieldTypeDeclaration(converter, field),
      required = field.field.required,
      `type` = converter.mustConvert(description(field), field.field.`type`),
    )
  }

  private[this] def fieldTypeDeclaration(types: ApiBuilderTypeToGraphQLConverter, field: ApiBuilderField): String = {
    val f = field.field
    (f.name, f.`type`, f.required) match {
      case ("id", ScalarType.StringType.name, true) => "ID"
      case _ => types.mustFindFieldTypeDeclaration(description(field), f.`type`)
    }
  }

  private[this] def description(field: ApiBuilderField): String = {
    s"Model '${field.model.qualified}' field '${field.field.name}'"
  }
}
