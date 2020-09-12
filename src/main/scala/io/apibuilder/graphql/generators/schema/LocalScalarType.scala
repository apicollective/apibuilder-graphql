package io.apibuilder.graphql.generators.schema

import io.apibuilder.validation.{ScalarType => ApiBuilderScalarType}

trait LocalScalarType {
  def apiBuilderType: io.apibuilder.validation.ScalarType
  def graphQLType: String
  def typeScriptType: String
}

class LocalScalarTypeImpl(
  override val apiBuilderType: ApiBuilderScalarType,
  override val graphQLType: String,
  override val typeScriptType: String,
) extends LocalScalarType

object LocalScalarType {
  val NativeGraphQLTypes = Set("Boolean", "Int", "String")

  case object BooleanType extends LocalScalarTypeImpl(ApiBuilderScalarType.BooleanType, "Boolean", "boolean")
  case object IntType extends LocalScalarTypeImpl(ApiBuilderScalarType.IntegerType, "Int", "number")
  case object StringType extends LocalScalarTypeImpl(ApiBuilderScalarType.StringType, "String", "string")
  case object DecimalType extends LocalScalarTypeImpl(ApiBuilderScalarType.DecimalType, "Decimal", "string")
  case object FloatType extends LocalScalarTypeImpl(ApiBuilderScalarType.FloatType, "Float", "number")
  case object LongType extends LocalScalarTypeImpl(ApiBuilderScalarType.LongType, "Long", "number")
  case object JsonType extends LocalScalarTypeImpl(ApiBuilderScalarType.JsonType, "Json", "any")
  case object ObjectType extends LocalScalarTypeImpl(ApiBuilderScalarType.ObjectType, "Object", "any")
  case object DateIso8601Type extends LocalScalarTypeImpl(ApiBuilderScalarType.DateIso8601Type, "DateIso8601", "string")
  case object DateTimeIso8601Type extends LocalScalarTypeImpl(ApiBuilderScalarType.DateTimeIso8601Type, "DateTimeIso8601", "string")
  case object UnitType extends LocalScalarTypeImpl(ApiBuilderScalarType.UnitType , "Unit", "undefined")

  val all: scala.List[LocalScalarType] = scala.List(BooleanType, IntType, StringType, DecimalType, FloatType, LongType, JsonType, ObjectType, DateIso8601Type, DateTimeIso8601Type, UnitType)

  private[this] val byGraphQLType: Map[String, LocalScalarType] = all.map(x => x.graphQLType.toLowerCase -> x).toMap

  def fromApiBuilderType(apiBuilderType: ApiBuilderScalarType): LocalScalarType = {
    apiBuilderType match {
      case ApiBuilderScalarType.BooleanType => BooleanType
      case ApiBuilderScalarType.DoubleType => FloatType
      case ApiBuilderScalarType.IntegerType => IntType
      case ApiBuilderScalarType.StringType => StringType
      case ApiBuilderScalarType.DecimalType => DecimalType
      case ApiBuilderScalarType.FloatType => FloatType
      case ApiBuilderScalarType.LongType => LongType
      case ApiBuilderScalarType.JsonType => JsonType
      case ApiBuilderScalarType.ObjectType => ObjectType
      case ApiBuilderScalarType.DateIso8601Type => DateIso8601Type
      case ApiBuilderScalarType.DateTimeIso8601Type => DateTimeIso8601Type
      case ApiBuilderScalarType.UnitType => UnitType
      case ApiBuilderScalarType.UuidType => StringType
    }
  }

  def fromApiBuilderTypeName(name: String): Option[LocalScalarType] = ApiBuilderScalarType.fromName(name).map(fromApiBuilderType)

  def fromGraphQLType(typeName: String): Option[LocalScalarType] = byGraphQLType.get(typeName.toLowerCase)

}
