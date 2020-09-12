package io.apibuilder.graphql.generators.query

import io.apibuilder.spec.v0.models._

trait ParameterHelpers {

  def allParametersWithBody(operation: Operation): List[Parameter] = {
    val (path, other) = operation.parameters.partition(_.location == ParameterLocation.Path)
    (path ++ operation.body.toSeq.map(toParam) ++ other).toList
  }

  private[this] def toParam(body: Body): Parameter = {
    Parameter(
      name = "body",
      required = true,
      `type` = body.`type`,
      location = ParameterLocation.Form,
      description = body.description,
      deprecation = body.deprecation,
      attributes = if (body.attributes.isEmpty) { None } else { Some(body.attributes) },
    )
  }
}
