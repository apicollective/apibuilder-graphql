package io.apibuilder.graphql

import cats.data.Validated.{Invalid, Valid}
import io.apibuilder.validation.MultiService
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

/**
 * Test the example(s) shown in readme
 */
class ReadmeSpec extends AnyWordSpec with Matchers {

  "usage" in {
    MultiService.fromUrls(
      List(
        "https://app.apibuilder.io/apicollective/apibuilder-common/latest/service.json",
        "https://app.apibuilder.io/apicollective/apibuilder-spec/latest/service.json",
        "https://app.apibuilder.io/apicollective/apibuilder-generator/latest/service.json",
        "https://app.apibuilder.io/apicollective/apibuilder-api/latest/service.json"
      )
    ) match {
      case Left(errors) => {
        println(s"Error downloading service: " + errors.mkString(", "))
      }
      case Right(multiService) => {
        GraphQLCodeGenerator.Default.generate(multiService) match {
          case Valid(schema) => {
            println("Schema is valid.")
            schema.files.foreach { f =>
              println(s"File: " + f.name)
              println(f.contents)
              println("")
            }
          }
          case Invalid(errors) => {
            println("Schema did not validate:")
            errors.toNonEmptyList.toList.foreach { error =>
              println(s" - ${error}")
            }
          }
        }
      }
    }
  }

}
