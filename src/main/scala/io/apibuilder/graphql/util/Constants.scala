package io.apibuilder.graphql.util

import io.apibuilder.graphql.schema.GraphQLIntent

object Constants {

  object GraphQLAttribute {
    val Name: String  = "graphql"
    object Fields {
      val Name: String = "name"
      val NameSpace: String = "namespace"
    }
  }

  object Resolvers {
    def includeNamespaces(intent: GraphQLIntent): Boolean = {
      intent match {
        case GraphQLIntent.Query => false
        case GraphQLIntent.Mutation => true
      }
    }
  }


}
