name := "apibuilder-graphql"

organization := "io.apibuilder"

ThisBuild / scalaVersion := "2.13.5"

lazy val allScalacOptions = Seq(
  "-deprecation",
  "-feature",
  "-Xfatal-warnings",
  "-unchecked",
  "-Xcheckinit",
  "-Xlint:adapted-args",
  "-Wconf:src=generated/.*:silent",
  "-Wconf:src=target/.*:silent", // silence the unused imports errors generated by the Play Routes
)

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
      "org.scalatest" %% "scalatest" % "3.2.8" % Test,
    ),
    testOptions += Tests.Argument("-oF"),
    scalacOptions ++= allScalacOptions,
  )

publishTo := {
  val host = "https://flow.jfrog.io/flow"
  if (isSnapshot.value) {
    Some("Artifactory Realm" at s"$host/libs-snapshot-local;build.timestamp=" + new java.util.Date().getTime)
  } else {
    Some("Artifactory Realm" at s"$host/libs-release-local")
  }
}

