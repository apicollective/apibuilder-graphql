package io.apibuilder.graphql.generators.query

import io.apibuilder.graphql.generators.helpers.QueryMutationHelpers
import io.apibuilder.graphql.schema.GraphQLIntent
import io.apibuilder.spec.v0.models.{Method, Service}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GraphQLQueryMutationTypeGeneratorMutationSpec extends AnyWordSpec with Matchers
  with QueryMutationHelpers {

  override val codeGenTestHelpersDir = "mutation"

  def userService(
    method: Method,
  ): Service = {
    val user = makeModel("user", plural = "users")
    val userForm = makeModel("user_form")
    makeService(
      namespace = "test",
      models = Seq(user, userForm),
      resources = Seq(
        makeResource(
          user.name,
          user.plural,
          operations = Seq(
            makeOperation(
              method,
              body = Some(makeBody(userForm.name)),
              path = "/users",
              attributes = Seq(
                makeGraphQLAttribute("users"),
              ),
              responses = Seq(
                make200Response(user.name),
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
