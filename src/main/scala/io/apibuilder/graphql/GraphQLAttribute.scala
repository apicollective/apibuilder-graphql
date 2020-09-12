package io.apibuilder.graphql

import io.apibuilder.spec.v0.models._
import play.api.libs.json.{JsObject, JsString}

/**
 * Represents the data in the ApiBuilder attribute named 'graphql'
 */
case class GraphQLAttribute(name: String)

object GraphQLAttribute {

  def fromOperation(op: Operation): Option[GraphQLAttribute] = {
    fromAttributes(op.attributes)
  }

  def fromAttributes(attributes: Seq[Attribute]): Option[GraphQLAttribute] = {
    attributes.find(_.name == util.Constants.GraphQLAttribute.Name)
      .map(_.value)
      .flatMap(fromJson)
  }

  private[this] def fromJson(js: JsObject): Option[GraphQLAttribute] = {
    (js \ util.Constants.GraphQLAttribute.Fields.Name).asOpt[JsString].map { name =>
      GraphQLAttribute(
        name = name.value,
      )
    }
  }
}

