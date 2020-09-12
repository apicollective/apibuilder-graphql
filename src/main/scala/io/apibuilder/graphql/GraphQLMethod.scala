package io.apibuilder.graphql

import io.apibuilder.graphql.schema.GraphQLIntent
import io.apibuilder.spec.v0.models.Method

/**
 * Represents an ApiBuilder operation Method
 */
// TODO: Maybe move to a static method in GraphQLIntent
case class GraphQLMethod(method: Method) {
  val intent: GraphQLIntent = method match {
    case Method.Get | Method.Delete | Method.Connect | Method.Head | Method.Options | Method.Trace | Method.UNDEFINED(_) => GraphQLIntent.Query
    case Method.Post | Method.Put | Method.Patch => GraphQLIntent.Mutation
  }
}

