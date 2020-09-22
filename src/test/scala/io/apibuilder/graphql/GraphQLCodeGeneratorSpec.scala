package io.apibuilder.graphql

import cats.data.Validated.{Invalid, Valid}
import io.apibuilder.graphql.generators.helpers.FileHelpers
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import io.apibuilder.validation.MultiService

class GraphQLCodeGeneratorSpec extends AnyWordSpec with Matchers
  with FileHelpers
{

  val SpecsUrl = "https://s3.amazonaws.com/io.flow.aws-s3-public/util/lib-apibuilder/specs.zip"

  "generates flow graphql schema" in {
    MultiService.fromUrl(SpecsUrl) match {
      case Left(errors) => sys.error(s"Error downloading ${SpecsUrl}: $errors")
      case Right(multiService) => {
        GraphQLCodeGenerator.Default.generate(multiService) match {
          case Valid(graphQLCode) => {
            println(s"Schema is valid")
            val dir = "src/test/resources/examples/flow"
            graphQLCode.files.foreach { f =>
              val path = s"$dir/${f.name}"
              println(s"  --> $path")
              writeToFile(path, f.contents)
            }
          }
          case Invalid(errors) => {
            println("GraphQL Schema is invalid. Cannot continue: " + errors.toNonEmptyList.toList)
          }
        }
      }
    }
  }
}
