package io.apibuilder.graphql.generators.schema

import io.apibuilder.graphql.schema.GraphQLType
import LocalScalarType._
import io.apibuilder.builders.ApiBuilderServiceBuilders
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ScalarGeneratorSpec extends AnyWordSpec with Matchers
  with ApiBuilderServiceBuilders
{

  def format(t: LocalScalarType): String = {
    GraphQLType.Scalar(t).formatted
  }

  "Boolean" in {
    format(BooleanType) must equal("scalar Boolean")
  }

  "Int" in {
    format(IntType) must equal("scalar Int")
  }

  "String" in {
    format(StringType) must equal("scalar String")
  }

  "Decimal" in {
    format(DecimalType) must equal("scalar Decimal")
  }

  "Float" in {
    format(FloatType) must equal("scalar Float")
  }

  "Long" in {
    format(LongType) must equal("scalar Long")
  }

  "Json" in {
    format(JsonType) must equal("scalar Json")
  }

  "Object" in {
    format(ObjectType) must equal("scalar Object")
  }

  "DateIso8601" in {
    format(DateIso8601Type) must equal("scalar DateIso8601")
  }

  "DateTimeIso8601" in {
    format(DateTimeIso8601Type) must equal("scalar DateTimeIso8601")
  }

}
