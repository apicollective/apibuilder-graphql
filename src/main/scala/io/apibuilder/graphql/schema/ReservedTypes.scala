package io.apibuilder.graphql.schema

/**
 * See https://spec.graphql.org/June2018/#sec-Executing-Operations
 * See Apollo server [https://www.apollographql.com/docs/apollo-server/data/file-uploads/]
 */
object ReservedTypes {

  val all = Set("Upload", "Mutation", "Query", "Subscription")

  private[this] val formatted = all.map(format)

  def contains(word: String): Boolean = formatted.contains(format(word))

  private[this] def format(v: String): String = v.trim.toLowerCase()

}




