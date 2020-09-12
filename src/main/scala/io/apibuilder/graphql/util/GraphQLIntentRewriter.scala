package io.apibuilder.graphql.util

import apibuilder.ApiBuilderHelper
import io.apibuilder.rewriter._
import io.apibuilder.graphql.{GraphQLAttribute, GraphQLOperation}
import io.apibuilder.graphql.schema.GraphQLIntent
import io.apibuilder.spec.v0.models.Attribute
import io.apibuilder.validation.{ApiBuilderType, MultiService, ScalarType}

object GraphQLIntentRewriter {

  def rewrite(multiService: MultiService, intent: GraphQLIntent): MultiService = {
    List(
      RenameTypes, ReduceTypes
    ).foldLeft(multiService) { case (ms, rewriter) =>
      rewriter.rewrite(ms, intent)
    }
  }

  trait IntentRewriter {
    def rewrite(multiService: MultiService, intent: GraphQLIntent): MultiService
  }

  /**
   * rename types based on the graphql 'name' attribute attached to enums, models or unions
   */
  object RenameTypes extends IntentRewriter {
    private[this] def attributeName(apiBuilderType: ApiBuilderType): Option[String] = {
      def name(attributes: Seq[Attribute]) = GraphQLAttribute.fromAttributes(attributes).map(_.name)
      apiBuilderType match {
        case e: ApiBuilderType.Enum => name(e.`enum`.attributes)
        case e: ApiBuilderType.Model => name(e.model.attributes)
        case e: ApiBuilderType.Union => name(e.union.attributes)
      }
    }

    override def rewrite(multiService: MultiService, intent: GraphQLIntent): MultiService = {
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

  /**
   * Collect the types references by the GraphQL Operations and reduce set of types to
   * only those referenced from them
   */
  object ReduceTypes extends IntentRewriter {
    override def rewrite(multiService: MultiService, intent: GraphQLIntent): MultiService = {
      val operationTypes = GraphQLOperation.all(multiService, intent).flatMap(_.originalTypes)
      MinimalTypesRewriter(operationTypes).rewrite(multiService)
    }
  }

}
