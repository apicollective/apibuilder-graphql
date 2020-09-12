package io.apibuilder.graphql.util

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TextSpec extends AnyWordSpec with Matchers {

  "wrapInQuotes" in {
    Text.wrapInQuotes("foo") must be(
      """"foo""""
    )
  }

  "camelCase" in {
    Text.camelCase("foo") must be("foo")
    Text.camelCase("foo_bar") must be("fooBar")
    Text.camelCase("foo_bar_baz") must be("fooBarBaz")
    Text.camelCase("fooBar_baz") must be("fooBarBaz")
    Text.camelCase("FOO") must be("foo")
    Text.camelCase("FOOBAR") must be("foobar")
  }

  "pascalCase" in {
    Text.pascalCase("foo_bar") must be("FooBar")
  }

  "allCaps" in {
    Text.allCaps("foo_bar") must be("FOO_BAR")
    Text.allCaps("foo bar") must be("FOO_BAR")
    Text.allCaps("foo-bar") must be("FOO_BAR")
  }
}
