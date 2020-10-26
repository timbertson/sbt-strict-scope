scalaVersion in ThisBuild := "2.12.10"

lazy val root = (project in file(".")).settings(
  organization := "net.gfxmonk",
  name := "sbt-strict-scope",
  version := IO.read(new File("VERSION")).trim,

  description := "Run commands with strict settings",
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),

  sbtPlugin := true,
  publishMavenStyle := false,
  bintrayRepository := "sbt-plugins",
  bintrayOrganization in bintray := None,
  bintrayVcsUrl := Some("git@github.com:timbertson/sbt-strict-scope.git"),
)

/**
 * Dumb manual test mechanism:
 * > project testProject
 * > testSettingPropagation (should only warn, because strict settings shouldn't be retained between tasks)
 * > testEarlyExit (should fail on fatal warning, and NOT print error from assertNotReached)
 */
lazy val assertNotReached = taskKey[Unit]("utility task for testEarlyExit")
lazy val testProject = (project in file("testProject")).settings(
  assertNotReached := {
    throw new MessageOnlyException("*** ERROR ***: this task should not have been reached, since the previous task should have caused early exit")
  },
  addCommandAlias("testEarlyExit", "; clean; strict compile; assertNotReached"),
  addCommandAlias("testSettingPropagation", "; strict clean; compile"),
)