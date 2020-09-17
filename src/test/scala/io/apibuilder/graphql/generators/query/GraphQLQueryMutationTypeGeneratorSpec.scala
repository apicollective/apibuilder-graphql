package io.apibuilder.graphql.generators.query

import io.apibuilder.graphql.generators.helpers.{CodeGenTestHelpers, GraphQLApiBuilderServiceHelpers}
import io.apibuilder.graphql.schema.GraphQLIntent
import io.apibuilder.spec.v0.models.{Method, Parameter, Service}
import io.apibuilder.validation.MultiService
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GraphQLQueryMutationTypeGeneratorSpec extends AnyWordSpec with Matchers
  with GraphQLApiBuilderServiceHelpers
  with CodeGenTestHelpers
{

  override val codeGenTestHelpersDir = "query"

  def verify(fileName: String, service: Service): Unit = {
    verify(fileName: String, makeMultiService(service))
  }

  def verify(fileName: String, multiService: MultiService): Unit = {
    mustMatchFile(
      fileName,
      GraphQLQueryMutationTypeGenerator(multiService).generate(GraphQLIntent.Query).get.formatted
    )
  }

  def verify(
    fileName: String,
    responseType: String = "user",
    parameters: Seq[Parameter] = Nil,
  ): Unit = {
    verify(
      fileName,
      userService(
        responseType = responseType,
        parameters = parameters,
      )
    )
  }

  private[this] val userModel = makeModel("user", plural = "users")

  def userService(
    responseType: String,
    modelType: String = "user",
    parameters: Seq[Parameter] = Nil,
  ): Service = {
    val userStatus = makeEnum("user_status")
    makeService(
      models = Seq(userModel),
      enums = Seq(userStatus),
      resources = Seq(
        makeResource(
          modelType,
          "users",
          operations = Seq(
            makeOperation(
              Method.Get,
              path = "/users",
              parameters = parameters,
              attributes = Seq(
                makeGraphQLAttribute("users"),
              ),
              responses = Seq(
                make200Response(responseType),
              )
            )
          )
        )
      )
    )
  }

  "GET /users" must {
    def verifyParam(fileName: String, p: Parameter) = {
      verify(
        fileName = fileName,
        responseType = "[user]",
        parameters = Seq(p)
      )
    }

    /*
    "no parameters" in {
      gen(
        responseType = "[user]",
      ) must equal(
        "users: [User!]"
      )
    }

    "array parameter" in {
      param(
        makeParameter("id", "[string]", required = false)
      ) must equal(
        "users(id: [String!]): [User!]"
      )
    }
    */

    "camelCase" in {
      verifyParam(
        "getUsersCamelCase",
        makeParameter("user_id", "string", required = false)
      )
    }
  }
}

    /*
    "default" must {
      "integer" must {
        "optional" in {
          param(
            makeParameter("limit", "integer", required = false, default = Some("25"))
          ) must equal(
            "users(limit: Int = 25): [User!]"
          )
        }

        "required" in {
          param(
            makeParameter("limit", "integer", required = true, default = Some("25"))
          ) must equal(
            "users(limit: Int! = 25): [User!]"
          )
        }

        "string" must {
          "optional" in {
            param(
              makeParameter("sort", "string", required = false, default = Some("-created_at"))
            ) must equal(
              """users(sort: String = "-created_at"): [User!]"""
            )
          }

          "required" in {
            param(
              makeParameter("sort", "string", required = true, default = Some("-created_at"))
            ) must equal(
              """users(sort: String! = "-created_at"): [User!]"""
            )
          }

          "quotes" in {
            param(
              makeParameter("sort", "string", required = true, default = Some("\"-created_at\""))
            ) must equal(
              """users(sort: String! = "\"-created_at\""): [User!]"""
            )
          }
        }
      }

      "enum" in {
        param(
          makeParameter("status", "user_status", required = false, default = Some("active"))
        ) must equal(
          """users(status: UserStatus = ACTIVE): [User!]"""
        )
      }
    }

    "optional" in {
      param(
        makeParameter("email", "string", required = false)
      ) must equal(
        "users(email: String): [User!]"
      )
    }

    "required" in {
      param(
        makeParameter("email", "string", required = true)
      ) must equal(
        "users(email: String!): [User!]"
      )
    }

    "path" must {
      "includes only path parameters in name of method" in {
        gen(
          parameters = Seq(
            makeParameter("id", "string", location = ParameterLocation.Path),
            makeParameter("status", "string", location = ParameterLocation.Query),
          )
        ) must equal(
          "users(id: String!, status: String!): User"
        )
      }

      "organization path parameter moved to the name of the method" in {
        gen(
          parameters = Seq(
            makeParameter("organization", "string", location = ParameterLocation.Path),
          )
        ) must equal(
          "users(organization: String!): User"
        )
      }

      "partner path parameter moved to the name of the method" in {
        gen(
          parameters = Seq(
            makeParameter("partner", "string", location = ParameterLocation.Path),
          )
        ) must equal(
          "users(partner: String!): User"
        )
      }
    }
  }

  "imports" must {
    "uses local name" in {
      val common = makeService(
        name = "common",
        namespace = "io.flow.common.v0",
        models = Seq(userModel),
      )
      val userSvc = userService(
        modelType = "io.flow.common.v0.models.user",
        responseType = "io.flow.common.v0.models.user",
      ).copy(
        imports = Seq(makeImport(common)),
      )

      gen(
        makeMultiService(Seq(common, userSvc)),
      ) must equal(
        Some("users: User")
      )
    }
  }
}
*/
