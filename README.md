# apibuilder-graphql

GraphQL Code Generator for ApiBuilder Services.

## Install

```
      "io.apibuilder" %% "apibuilder-graphql" % "0.0.2"
```

## Usage

```
    import cats.data.Validated.{Invalid, Valid}
    import io.apibuilder.validation.MultiService
    import io.apibuilder.graphql.GraphQLCodeGenerator

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
            schema.invocation.files.foreach { f =>
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
```

