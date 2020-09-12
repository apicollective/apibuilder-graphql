package io.apibuilder.graphql.generators.schema

import io.apibuilder.graphql.generators.helpers
import io.apibuilder.graphql.generators.helpers.ApiBuilderTypeToGraphQLConverterHelpers
import io.apibuilder.spec.v0.models
import io.apibuilder.spec.v0.models.Service
import io.apibuilder.validation.{ApiBuilderService, ApiBuilderType}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class UnionGeneratorSpec extends AnyWordSpec with Matchers
  with helpers.GraphQLApiBuilderServiceHelpers
  with ApiBuilderTypeToGraphQLConverterHelpers
{
  private[this] val generator: UnionGenerator = UnionGenerator()
  private[this] val Namespace = "testns"

  private[this] def gen(service: Service, union: models.Union): String = {
    val t = generator.generate(
      makeConverter(makeMultiService(
        addOperationsForAllModels(service),
      )),
      ApiBuilderType.Union(ApiBuilderService(service), union),
    ).formatted
    println(s"\n\n")
    println(t)
    println(s"\n\n")
    t
  }

  "generates schema" in {
    val scifi = makeModel(name = "Scifi")
    val history = makeModel(name = "history")
    val union = makeUnion(
      name = "genre",
      types = Seq(
        makeUnionType(scifi.name),
        makeUnionType(history.name),
      )
    )

    gen(
      makeService(namespace = Namespace, unions = Seq(union), models = Seq(scifi, history)),
      union
    ) must equal(
      """# testns.unions.genre
        |union Genre = Scifi | History""".stripMargin
      )
  }

}
