package io.apibuilder.graphql.schema

import cats.data.ValidatedNec
import cats.implicits._

import scala.annotation.tailrec

case class SchemaValidator() {
  private[this] type ValidationResult[T] = ValidatedNec[String, T]

  def validate(types: Seq[GraphQLType]): ValidationResult[GraphQLTypeSchema] = {
    validateGraphQLTypes(types).andThen { validatedTypes =>
      GraphQLTypeSchema(
        validatedTypes,
      ).validNec
    }
  }

  private[this] def validateGraphQLTypes(graphQLTypes: Seq[GraphQLType]): ValidationResult[Seq[GraphQLType]] = {
    graphQLTypes.map(validateGraphQLType).toList.traverse(identity).andThen { types =>
      validateGraphQLTypeNameAreUnique(types)
    }
  }

  @tailrec
  private[this] def validateGraphQLType(graphQLType: GraphQLType): ValidationResult[GraphQLType] = {
    graphQLType match {
      case f: GraphQLType.Array => validateGraphQLType(f.`type`)
      case f: GraphQLType.Type => validateGraphQLTypeNamed(f, f.name)
      case f: GraphQLType.Input => validateGraphQLTypeNamed(f, f.name)
      case f: GraphQLType.Enum => validateGraphQLTypeNamed(f, f.name)
      case f: GraphQLType.Union => validateGraphQLTypeNamed(f, f.name)
      case _: GraphQLType.Scalar | _: GraphQLType.Query | _: GraphQLType.Mutation => graphQLType.validNec
    }
  }

  private[this] def validateGraphQLTypeNamed(graphQLType: GraphQLType, name: String): ValidationResult[GraphQLType] = {
    if (ReservedTypes.all.contains(name)) {
      s"Type name '${name}' is a built in type and cannot be used".invalidNec
    } else {
      graphQLType.validNec
    }
  }

  private[this] def validateGraphQLTypeNameAreUnique(graphQLTypes: Seq[GraphQLType]): ValidationResult[Seq[GraphQLType]] = {
    val duplicates = graphQLTypes.collect { case f: NamedGraphQLType => f }
      .groupBy(_.name)
      .filter { case (_, v) => v.length > 1 }

    if (duplicates.isEmpty) {
      graphQLTypes.validNec
    } else {
      val errors = duplicates.map { case (name, types) =>
        s"  - $name: " + types.map(_.name).sorted.mkString(", ")
      }.toList.sorted.mkString("\n")
      s"The following type name(s) are duplicated. All type names must be unique:\n$errors".invalidNec
    }
  }

}
