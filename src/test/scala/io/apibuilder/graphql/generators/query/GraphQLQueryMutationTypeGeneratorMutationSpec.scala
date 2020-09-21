package io.apibuilder.graphql.generators.query

import io.apibuilder.graphql.generators.helpers.QueryMutationHelpers
import io.apibuilder.graphql.schema.GraphQLIntent
import io.apibuilder.spec.v0.models.{Method, Model, Service}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GraphQLQueryMutationTypeGeneratorMutationSpec extends AnyWordSpec with Matchers
  with QueryMutationHelpers {

  override val codeGenTestHelpersDir = "mutation"

  private[this] val userModel = makeModel("user", plural = "users")

  def userService(
    method: Method,
    bodyType: Option[Model] = None,
  ): Service = {
    makeService(
      namespace = "test",
      models = Seq(userModel),
      resources = Seq(
        makeResource(
          userModel.name,
          userModel.plural,
          operations = Seq(
            makeOperation(
              method,
              body = bodyType.map { b => makeBody(b.name) },
              path = "/users",
              attributes = Seq(
                makeGraphQLAttribute("users"),
              ),
              responses = Seq(
                make200Response(userModel.name),
              )
            )
          )
        )
      )
    )
  }

  "POST /users" must {
    verify(
      fileName = "postUsers",
      service = userService(Method.Post),
      intent = GraphQLIntent.Mutation,
    )
  }
}
