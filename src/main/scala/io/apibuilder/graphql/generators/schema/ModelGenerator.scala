package io.apibuilder.graphql.generators.schema

import io.apibuilder.graphql.schema.{GraphQLIntent, GraphQLType, GraphQLTypeField}
import io.apibuilder.validation.{ApiBuilderField, ApiBuilderType, ScalarType}

object ModelGenerator {
  case class Container(typ: String, qualified: String)

  object Container {
    def apply(m: ApiBuilderType.Model): Container = Container("Model", m.qualified)
    def apply(i: ApiBuilderType.Interface): Container = Container("Interface", i.qualified)
  }
}

case class ModelGenerator() {
  import ModelGenerator.Container

  def generate(converter: ApiBuilderTypeToGraphQLConverter, model: ApiBuilderType.Model): GraphQLType = {
    val fields = model.fields.map { f => generateField(converter, f)(Container(model)) }
    converter.intent match {
      case GraphQLIntent.Query => GraphQLType.Type(model, fields)
      case GraphQLIntent.Mutation => GraphQLType.Input(model, fields)
    }
  }

  // Treat same as model based on ApiBuilderType.Interface.typeDiscriminator returning TypeDiscriminator.Models
  def generate(converter: ApiBuilderTypeToGraphQLConverter, interface: ApiBuilderType.Interface): GraphQLType = {
    val fields = interface.fields.map { f => generateField(converter, f)(Container(interface)) }
    converter.intent match {
      case GraphQLIntent.Query => GraphQLType.Type(interface, fields)
      case GraphQLIntent.Mutation => GraphQLType.Input(interface, fields)
    }
  }

  private def generateField(converter: ApiBuilderTypeToGraphQLConverter, field: ApiBuilderField)(implicit container: Container): GraphQLTypeField = {
    GraphQLTypeField(
      originalName = field.field.name,
      declaration = fieldTypeDeclaration(converter, field),
      required = field.field.required,
      `type` = converter.mustConvert(description(field), field.field.`type`),
    )
  }

  private def fieldTypeDeclaration(types: ApiBuilderTypeToGraphQLConverter, field: ApiBuilderField)(implicit container: Container): String = {
    val f = field.field
    (f.name, f.`type`, f.required) match {
      case ("id", ScalarType.StringType.name, true) => "ID"
      case _ => types.mustFindFieldTypeDeclaration(description(field), f.`type`)
    }
  }

  private def description(field: ApiBuilderField)(implicit container: Container): String = {
    s"${container.typ} '${container.qualified}' field '${field.field.name}'"
  }
}
