import ScalaProject._


lazy val root = (project in file(".")).settings(
  publicProjectSettings,
  name := "sbt-strict-scope",
  description := "Run commands with strict settings",
  sbtPlugin := true,
)

/**
 * Dumb manual test mechanism, just run these one by one and look at the output:
 */

// compiles with warning
addCommandAlias("testPass1", "; project testProject; strict clean; compile")

/* Prints:
DUMMY (ThisBuild): Dummy value in default mode
DUMMY (testProject): Dummy value for testProject
---
DUMMY (ThisBuild): Dummy value in default mode
DUMMY (testProject): Dummy value in STRICT mode
*/
addCommandAlias("testPass2", "; project aggregateProject; dumpDummy; strict dumpDummy")

// includes -Xfatal-warnings in the second listing
addCommandAlias("testPass3", "; project aggregateProject; dumpScalac; strict dumpScalac")

addCommandAlias("testPass4", "; project wartProject; dumpScalac; clean; compile")

// fails direct compilation
addCommandAlias("testFail1", "; project testProject; clean; strict compile; assertNotReached")

// fails due to dependsOn(...)
addCommandAlias("testFail2", "; project aggregateProject; testProject / clean; strict compile")

// fails due to aggregates(...)
addCommandAlias("testFail3", "; project dependencyProject; testProject / clean; strict compile")

addCommandAlias("testFail4", "; project wartProject; strict dumpScalac; clean; strict compile")

lazy val dummySetting = taskKey[String]("dummy setting")

lazy val dumpScalac = taskKey[Unit]("dump state")
ThisBuild / dumpScalac := {
  println("SCALAC: " + (Compile / scalacOptions).value)
}

lazy val dumpDummy = taskKey[Unit]("dump state")
/*
You might expect `dummySetting.value` here to refer to the value of `dummySetting` in `testProject`
when you run `testProject / dumpDummy`. But nope:

> In sbt, however, scope delegation can delegate a scope to a more general scope, like a project-level
setting to a build-level settings, but that build-level setting cannot refer to the project-level setting.
https://www.scala-sbt.org/1.x/docs/Scope-Delegation.html
*/
ThisBuild / dumpDummy := {
  println("DUMMY (ThisBuild): " + dummySetting.value)
}

ThisBuild / dummySetting := "Dummy value in default mode"

lazy val assertNotReached = taskKey[Unit]("utility task for testEarlyExit")
lazy val testProject = (project in file("testProject")).settings(
  hiddenProjectSettings,
  strictSettings ++= Seq(
    dummySetting := "Dummy value in STRICT mode",
  ),
  dummySetting := "Dummy value for testProject",
  dumpDummy := {
    println("DUMMY (testProject): " + dummySetting.value)
  },
  dumpScalac := {
    println("SCALAC: " + (Compile / scalacOptions).value)
  },
  assertNotReached := {
    throw new MessageOnlyException("*** ERROR ***: this task should not have been reached, since the previous task should have caused early exit")
  },
)

lazy val wartProject = (project in file("testProject/wartProject")).settings(
  hiddenProjectSettings,
  strictSettings ++= Seq(
    wartremoverErrors := Warts.all,
  ),
  wartremoverWarnings ++= Warts.all,
  dumpScalac := {
    println("SCALAC: " + (Compile / scalacOptions).value)
  },
)

lazy val aggregateProject = (project in file("testProject/aggregate"))
  .settings(hiddenProjectSettings)
  .aggregate(testProject)

lazy val dependencyProject = (project in file("testProject/dependency"))
  .settings(hiddenProjectSettings)
  .dependsOn(testProject)
