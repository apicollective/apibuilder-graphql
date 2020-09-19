package io.apibuilder.graphql.util

import apibuilder.ApiBuilderHelper
import io.apibuilder.rewriter.{MultiServiceRewriter, TypeRewriter}
import io.apibuilder.graphql.GraphQLAttribute
import io.apibuilder.graphql.schema.GraphQLIntent
import io.apibuilder.spec.v0.models.Attribute
import io.apibuilder.validation.{ApiBuilderType, MultiService, ScalarType}

/**
 * rename types based on the graphql 'name' attribute attached to enums, models or unions
 */
case class RenameTypesByAttributeRewriter(intent: GraphQLIntent) extends MultiServiceRewriter {
  private[this] def attributeName(apiBuilderType: ApiBuilderType): Option[String] = {
    def name(attributes: Seq[Attribute]) = GraphQLAttribute.fromAttributes(attributes).map(_.name)
    apiBuilderType match {
      case e: ApiBuilderType.Enum => name(e.`enum`.attributes)
      case e: ApiBuilderType.Model => name(e.model.attributes)
      case e: ApiBuilderType.Union => name(e.union.attributes)
    }
  }

  override def rewrite(multiService: MultiService): MultiService = {
    val nameMapping: Map[String, String] = multiService.allTypes.flatMap { t =>
      attributeName(t) match {
        case Some(newName) if newName != t.name => Some(t.qualified -> newName)
        case _ => None
      }
    }.toMap

    TypeRewriter {
      case t: ScalarType => t
      case t: ApiBuilderType => {
        nameMapping.get(t.qualified) match {
          case None => t
          case Some(newName) => ApiBuilderHelper.changeName(t, newName)
        }
      }
    }.rewrite(multiService)
  }
}
