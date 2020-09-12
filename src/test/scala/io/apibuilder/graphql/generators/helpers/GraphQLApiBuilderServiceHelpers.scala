package io.apibuilder.graphql.generators.helpers

import io.apibuilder.graphql.util.Constants
import io.apibuilder.builders.{ApiBuilderServiceBuilders, MultiServiceBuilders}
import io.apibuilder.spec.v0.models.{Attribute, Model, Resource, Service}
import play.api.libs.json.Json

trait GraphQLApiBuilderServiceHelpers extends ApiBuilderServiceBuilders with MultiServiceBuilders {

  def addOperationsForAllModels(service: Service): Service = {
    val allModels = service.models
    val existingTypes = service.resources.flatMap(_.operations).flatMap(_.responses.map(_.`type`)).toSet
    val missingModels = allModels.filterNot { m => existingTypes.contains(m.name) }

    def res(m: Model): Resource = {
      makeResource(
        m.name,
        m.plural,
        operations = Seq(
          makeOperation(
            responses = Seq(make200Response(m.name)),
            attributes = Seq(makeGraphQLAttribute(m.plural))
          )
        )
      )
    }

    service.copy(
      resources = service.resources ++ missingModels.map(res)
    )
  }

  def makeGraphQLAttribute(
    name: String,
  ): Attribute = {
    import Constants.GraphQLAttribute._
    makeAttribute(
      name = Name,
      value = Json.obj(
        Fields.Name -> name,
      ),
    )
  }

}

