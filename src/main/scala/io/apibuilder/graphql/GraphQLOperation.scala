package io.apibuilder.graphql

import apibuilder.{ApiBuilderHelper, ApiBuilderHelperImpl}
import io.apibuilder.graphql.schema.GraphQLIntent
import io.apibuilder.spec.v0.models._
import io.apibuilder.validation.{AnyType, ApiBuilderService, ApiBuilderType, MultiService}

case class InternalResource(
  resource: Resource,
  `type`: AnyType,
)

/**
 * Represents an ApiBuilder operation that has a graphql attribute specified
 */
case class GraphQLOperation(
  multiService: MultiService,
  service: ApiBuilderService,
  graphQLIntent: GraphQLIntent,
  resource: InternalResource,
  originalOperation: Operation,
  response: Response,
  attribute: GraphQLAttribute,
) {
  private[this] val helper: ApiBuilderHelper = ApiBuilderHelperImpl(multiService)
  val methodIntent: GraphQLIntent = GraphQLIntent(originalOperation.method)

  lazy val intentOperation: Operation = graphQLIntent match {
    case GraphQLIntent.Query => operationForQuery
    case GraphQLIntent.Mutation => operationForMutation
  }

  lazy val originalTypes: Seq[ApiBuilderType] = allTypes(originalOperation)

  // Returns the list of types to be used for the Graph QL Intent
  lazy val intentTypes: Seq[ApiBuilderType] = allTypes(intentOperation)

  private[this] def allTypes(operation: Operation): Seq[ApiBuilderType] = {
    (
      operation.responses.map(_.`type`) ++
        operation.body.toSeq.map(_.`type`) ++
        operation.parameters.map(_.`type`)
      ).flatMap { t =>
      helper.resolveType(service, t)
    }.distinct.collect { case t: ApiBuilderType => t }
  }

  private[this] def buildOperation(
    body: Option[Body],
    parameters: Seq[Parameter],
    responses: Seq[Response],
  ): Operation = {
    originalOperation.copy(
      parameters = parameters,
      body = body,
      responses = responses,
    )
  }

  private[this] def operationForQuery: Operation = {
     methodIntent match {
      case GraphQLIntent.Query => buildOperation(
        body = originalOperation.body,
        parameters = originalOperation.parameters,
        responses = originalOperation.responses,
      )
      case GraphQLIntent.Mutation => buildOperation(
        body = None,
        parameters = Nil,
        responses = originalOperation.responses,
      )
    }
  }

  private[this] def operationForMutation: Operation = {
    methodIntent match {
      case GraphQLIntent.Query => buildOperation(
        body = None,
        parameters = Nil,
        responses = Nil,
      )
      case GraphQLIntent.Mutation => buildOperation(
        body = originalOperation.body,
        parameters = originalOperation.parameters,
        responses = Nil,
      )
    }
  }

  private[this] lazy val code: String = {
    response.code match {
      case ResponseCodeInt(v) => v.toString
      case ResponseCodeUndefinedType(v) => v
      case ResponseCodeOption.Default => "*"
      case ResponseCodeOption.UNDEFINED(v) => v
    }
  }

  lazy val description = s"Operation '${originalOperation.method} ${originalOperation.path}' response '${code}'"
}

object GraphQLOperation {
  def all(service: ApiBuilderService, intent: GraphQLIntent): Seq[GraphQLOperation] = {
    all(MultiService(List(service)), intent)
  }

  def all(ms: MultiService, intent: GraphQLIntent): Seq[GraphQLOperation] = {
    ms.services().flatMap { service =>
      service.service.resources.flatMap { resource =>
        resource.operations.flatMap { operation =>
          GraphQLAttribute.fromOperation(operation).toSeq.flatMap { attribute =>
            assertHas2xxResponse(operation)
            operation.responses.map { response =>
              GraphQLOperation(
                multiService = ms,
                service = service,
                graphQLIntent = intent,
                resource = toInternalResource(ms, service, resource),
                originalOperation = operation,
                attribute = attribute,
                response = response,
              )
            }
          }
        }
      }
    }
  }

  private[this] def toInternalResource(ms: MultiService, service: ApiBuilderService, resource: Resource) = {
    InternalResource(
      resource,
      ms.findType(service.namespace, resource.`type`).getOrElse {
        sys.error(s"Failed to resolve resource type '${resource.`type`}'")
      }
    )
  }

  /**
   * If we find an operation with a 'graphql' attribute, it is intended to be exposed via GraphQL and thus
   * must have at least one response. Likely indicates user error (attribute needs to be removed) or a bug
   * in how we rewrite the multi services.
   */
  private[this] def assertHas2xxResponse(operation: Operation): Unit = {
    assert(
      operation.responses.exists(ApiBuilderHelper.is2xx),
      s"Operation '${operation.method} ${operation.path}' w/ attribute 'graphql' must have a response with 2xx status code",
    )
  }
}
