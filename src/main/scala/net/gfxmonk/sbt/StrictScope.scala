package net.gfxmonk.sbt
import sbt._
import sbt.Keys._

object StrictScopePlugin extends AutoPlugin {
  object autoImport {
    val strictScope = StrictScope.strictScope
  }

  override def trigger: PluginTrigger = NoTrigger

  override val projectSettings = StrictScope.defaultSettings
}

object StrictScope {
  val strictScope = taskKey[Seq[Setting[_]]]("Settings to apply for the `strict` command")

  val defaultSettings = Seq(
    strictScope := defaultMakeStrict,
    commands += command,
  )

  // default values for the tasks and settings
  def defaultMakeStrict: Seq[Setting[_]] = {
     Seq(scalacOptions += "-Xfatal-warnings")
  }

  def command = Command.single("strict") { (state0, scoped) =>
    val (state1, extraSettings) = Project.extract(state0).runTask(strictScope, state0)
    val state2 = Project.extract(state1).appendWithoutSession(extraSettings, state1)
    val state3 = Command.process(scoped, state2)
    // Returning state3 makes the setting permanent, which we were trying to avoid.
    // Copying the `next` property appears to propagate failure correctly, but leave the
    // rest of the state alone.
    state0.copy(next=state3.next)
  }
}
