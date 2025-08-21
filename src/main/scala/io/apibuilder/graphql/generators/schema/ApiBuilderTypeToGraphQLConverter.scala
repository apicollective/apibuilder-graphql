package io.apibuilder.graphql.generators.schema

import apibuilder.ApiBuilderHelper
import io.apibuilder.graphql.GraphQLOperation
import io.apibuilder.graphql.schema.{GraphQLIntent, GraphQLType}
import io.apibuilder.graphql.util.Text
import io.apibuilder.validation.{AnyType, ApiBuilderType, MultiService, ScalarType}

case class ApiBuilderTypeToGraphQLConverter(
  multiService: MultiService,
  intent: GraphQLIntent,
  namespace: String,
)(
  implicit context: List[String] = Nil,
) {

  private[this] val modelGenerator: ModelGenerator = ModelGenerator()
  private[this] val unionGenerator: UnionGenerator = UnionGenerator()

  def fieldTypeDeclaration(t: AnyType): String = {
    t match {
      case s: ScalarType => LocalScalarType.fromApiBuilderType(s).graphQLType
      case _ => Text.pascalCase(t.name)
    }
  }

  def mustFindFieldTypeDeclaration(description: String, v: String): String = {
    fieldTypeDeclaration(v).getOrElse {
      def debug(contents: Seq[String]): Unit = {
        println("-------")
        println(contents.sorted.mkString("\n"))
      }

      debug(GraphQLOperation.all(multiService, intent).map { op =>
        s" - op: ${op.description}: ${op.response} -- ${op.intentTypes.map(_.qualified)}"
      })
      debug(multiService.allTypes.filter(_.name == "public_key").map { t =>
        s" - type: ${t.qualified}"
      })
      sys.error(s"Intent '${intent}' $description: Failed to find field declaration for type '$v'")
    }
  }

  private[this] def fieldTypeDeclaration(v: String): Option[String] = {
    findType(v) match {
      case Some(t) => Some(fieldTypeDeclaration(t))
      case None => {
        v match {
          case ApiBuilderHelper.Array(inner) => fieldTypeDeclaration(inner).map { t => s"[$t!]" }
          case ApiBuilderHelper.Map(_) => Some(LocalScalarType.ObjectType.graphQLType)
          case _ => None
        }
      }
    }
  }

  def convert(t: AnyType): GraphQLType = {
    t match {
      case s: ScalarType => GraphQLType.Scalar(LocalScalarType.fromApiBuilderType(s))
      case v: ApiBuilderType.Enum => trace("enum", v.name); GraphQLType.Enum(v)
      case v: ApiBuilderType.Model => trace("model", v.name); withNamespace(v) { converter =>
        modelGenerator.generate(converter, v)
      }
      case v: ApiBuilderType.Union => trace("union", v.name); withNamespace(v) { converter =>
        unionGenerator.generate(converter, v)
      }
      case v: ApiBuilderType.Interface => trace("interface", v.name); withNamespace(v) { converter =>
        modelGenerator.generate(converter, v)
      }
    }
  }

  def mustConvert(description: String, v: String): GraphQLType = {
    convert(v).getOrElse {
      sys.error(s"Intent '${intent}' $description: Failed to find convert type '$v'")
    }
  }

  private[this] def convert(v: String): Option[GraphQLType] = {
    findType(v) match {
      case None => {
        v match {
          case ApiBuilderHelper.Array(inner) => convert(inner).map(GraphQLType.Array)
          case ApiBuilderHelper.Map(_) => Some(GraphQLType.Scalar(LocalScalarType.ObjectType))
          case _ => None
        }
      }
      case Some(t) => Some(convert(t))
    }
  }

  private[this] val TraceEnabled = false

  private[this] def trace(typ: String, name: String): Unit = {
    if (TraceEnabled) {
      val ctx = if (context.isEmpty) {
        ""
      } else {
        context.mkString("/") + ": "
      }
      println(s" - generate ${ctx}$typ $name")
    }
  }

  private[this] def findType(typeName: String): Option[AnyType] = {
    multiService.findType(namespace, typeName)
  }

  private[this] def withNamespace[T](typ: ApiBuilderType)(f: ApiBuilderTypeToGraphQLConverter => T): T = {
    f(
      this.copy(
        namespace = typ.namespace,
      )(
        context = this.context ++ List(typ.name),
      )
    )
  }

}
