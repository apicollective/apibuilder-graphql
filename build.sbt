name := "apibuilder-graphql"

organization := "io.apibuilder"

scalaVersion in ThisBuild := "2.13.3"

lazy val resolversSettings = Seq(
  resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  resolvers += "Artifactory" at "https://flow.jfrog.io/flow/libs-release/",
  credentials += Credentials(
    "Artifactory Realm",
    "flow.jfrog.io",
    System.getenv("ARTIFACTORY_USERNAME"),
    System.getenv("ARTIFACTORY_PASSWORD")
  )
)

lazy val root = project
  .in(file("."))
  .settings(resolversSettings)
  .settings(
    libraryDependencies ++= Seq(
      "io.apibuilder" %% "apibuilder-validation" % "0.4.28",
      "org.typelevel" %% "cats-core" % "2.1.1",
      "org.scalatest" %% "scalatest" % "3.2.1" % Test,
      compilerPlugin("com.github.ghik" %% "silencer-plugin" % "1.7.0" cross CrossVersion.full),
      "com.github.ghik" %% "silencer-lib" % "1.7.0" % Provided cross CrossVersion.full,
    ),
    testOptions += Tests.Argument("-oF"),
    // silence all warnings on autogenerated files
    scalacOptions += "-P:silencer:pathFilters=src/main/scala/io/apibuilder/generated/.*;src/test/scala/io/apibuilder/generated/.*"
  )


publishTo := {
  val host = "https://flow.jfrog.io/flow"
  if (isSnapshot.value) {
    Some("Artifactory Realm" at s"$host/libs-snapshot-local;build.timestamp=" + new java.util.Date().getTime)
  } else {
    Some("Artifactory Realm" at s"$host/libs-release-local")
  }
}

version := "0.0.6"
