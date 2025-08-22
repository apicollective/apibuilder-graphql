package io.apibuilder.graphql.generators.schema

import io.apibuilder.graphql.schema.GraphQLType
import io.apibuilder.builders.ApiBuilderServiceBuilders
import io.apibuilder.spec.v0.models
import io.apibuilder.validation.{ApiBuilderService, ApiBuilderType}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class EnumGeneratorSpec extends AnyWordSpec with Matchers
  with ApiBuilderServiceBuilders
{

  private[this] val Namespace = "testns"

  private[this] def gen(`enum`: models.Enum): String = {
    GraphQLType.Enum(
      ApiBuilderType.Enum(
        ApiBuilderService(makeService(enums = Seq(`enum`), namespace = Namespace)),
        `enum`,
      )
    ).formatted
  }

  "generates schema" in {
    gen(
      makeEnum(
        name = "Episode",
        plural = "Episodes",
        values = Seq(
          makeEnumValue(name = "NEWHOPE"),
          makeEnumValue(name = "EMPIRE"),
          makeEnumValue(name = "JEDI"),
        )
      )
    ) must equal(
      """# testns.enums.Episode
        |enum Episode {
        |  NEWHOPE
        |  EMPIRE
        |  JEDI
        |}""".stripMargin)
  }

  "prefers value when specified" in {
    gen(
      makeEnum(
        name = "Episode",
        plural = "Episodes",
        values = Seq(
          makeEnumValue(name = "NEWHOPE", value = Some("NH")),
          makeEnumValue(name = "EMPIRE"),
        )
      )
    ) must equal(
      """# testns.enums.Episode
        |enum Episode {
        |  NH
        |  EMPIRE
        |}""".stripMargin)
  }

}
