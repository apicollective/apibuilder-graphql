package io.apibuilder.graphql.generators.query

import io.apibuilder.graphql.generators.helpers.QueryMutationHelpers
import io.apibuilder.graphql.schema.GraphQLIntent
import io.apibuilder.spec.v0.models.{Method, Parameter, ParameterLocation, Service}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GraphQLQueryMutationTypeGeneratorQuerySpec extends AnyWordSpec with Matchers
  with QueryMutationHelpers
{

  override val codeGenTestHelpersDir = "query"

  def verify(
    fileName: String,
    intent: GraphQLIntent,
    responseType: String = "user",
    parameters: Seq[Parameter] = Nil,
  ): Unit = {
    verify(
      fileName,
      userService(
        responseType = responseType,
        parameters = parameters,
      ),
      intent,
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
      namespace = "test",
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
    def verifyParam(fileName: String, p: Parameter): Unit = {
      verify(
        fileName = fileName,
        intent = GraphQLIntent.Query,
        responseType = "[user]",
        parameters = Seq(p)
      )
    }

    "no parameters" in {
      verify(
        "getUsersNoParameters",
        intent = GraphQLIntent.Query,
        responseType = "[user]",
      )
    }

    "array parameter" in {
      verifyParam(
        "getUsersArrayParameter",
        makeParameter("id", "[string]", required = false)
      )
    }

    "camelCase" in {
      verifyParam(
        "getUsersCamelCase",
        makeParameter("user_id", "string", required = false)
      )
    }

    "default" must {
      "integer" must {
        "optional" in {
          verifyParam(
            "getUsersDefaultIntegerOptional",
            makeParameter("limit", "integer", required = false, default = Some("25"))
          )
        }

        "required" in {
          verifyParam(
            "getUsersDefaultRequired",
            makeParameter("limit", "integer", required = true, default = Some("25"))
          )
        }

        "string" must {
          "optional" in {
            verifyParam(
              "getUsersDefaultStringOptional",
              makeParameter("sort", "string", required = false, default = Some("-created_at"))
            )
          }

          "required" in {
            verifyParam(
              "getUsersDefaultStringRequired",
              makeParameter("sort", "string", required = true, default = Some("-created_at"))
            )
          }

          "quotes" in {
            verifyParam(
              "getUsersDefaultStringQuotes",
              makeParameter("sort", "string", required = true, default = Some("\"-created_at\""))
            )
          }
        }
      }

      "enum" in {
        verifyParam(
          "getUsersEnum",
          makeParameter("status", "user_status", required = false, default = Some("active"))
        )
      }
    }

    "optional" in {
      verifyParam(
        "getUsersOptional",
        makeParameter("email", "string", required = false)
      )
    }

    "required" in {
      verifyParam(
        "getUsersRequired",
        makeParameter("email", "string", required = true)
      )
    }

    "path" must {
      "includes only path parameters in name of method" in {
        verify(
          "getUsersPathIncludesOnlyPathParametersInNameOfMethod",
          intent = GraphQLIntent.Query,
          parameters = Seq(
            makeParameter("id", "string", location = ParameterLocation.Path),
            makeParameter("status", "string", location = ParameterLocation.Query),
          )
        )
      }

      "organization path parameter moved to the name of the method" in {
        verify(
          "getUsersPathOrganizationPathParameterMovedToTheNameOfTheMethod",
          intent = GraphQLIntent.Query,
          parameters = Seq(
            makeParameter("organization", "string", location = ParameterLocation.Path),
          )
        )
      }

      "partner path parameter moved to the name of the method" in {
        verify(
          "getUsersPathPartnerPathParameterMovedToTheNameOfTheMethod",
          intent = GraphQLIntent.Query,
          parameters = Seq(
            makeParameter("partner", "string", location = ParameterLocation.Path),
          )
        )
      }
    }
  }

  "imports" must {
    "use local name" in {
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

      verify(
        fileName = "importsUserLocalName",
        intent = GraphQLIntent.Query,
        multiService = makeMultiService(Seq(common, userSvc)),
      )
    }
  }
}

