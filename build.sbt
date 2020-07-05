scalaVersion in ThisBuild := "2.12.10"

lazy val root = (project in file(".")).settings(
  organization := "net.gfxmonk",
  name := "sbt-strict-scope",
  version := "1.1.0",

  description := "Run commands with strict settings",
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),

  sbtPlugin := true,
  publishMavenStyle := false,
  bintrayRepository := "sbt-plugins",
  bintrayOrganization in bintray := None,
  bintrayVcsUrl := Some("git@github.com:timbertson/sbt-strict-scope.git"),
)
