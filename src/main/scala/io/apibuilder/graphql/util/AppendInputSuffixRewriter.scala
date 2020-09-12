package io.apibuilder.graphql.util

import apibuilder.ApiBuilderHelper
import io.apibuilder.rewriter._
import io.apibuilder.validation.{ApiBuilderType, MultiService, ScalarType}

object AppendInputSuffixRewriter extends MultiServiceRewriter {

  override def rewrite(multiService: MultiService): MultiService = {
    TypeRewriter {
      case t: ScalarType => t
      case t: ApiBuilderType.Enum => t
      case t: ApiBuilderType.Model => addSuffix(t)
      case t: ApiBuilderType.Union => addSuffix(t)
    }.rewrite(multiService)
  }

  private[this] def addSuffix(t: ApiBuilderType): ApiBuilderType = {
    ApiBuilderHelper.changeName(t, t.name + "_" + MultiServiceView.InputSuffix)
  }

}
