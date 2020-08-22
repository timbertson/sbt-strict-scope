package net.gfxmonk.sbt
import sbt._
import sbt.Keys._

object StrictScopePlugin extends AutoPlugin {
  object autoImport {
    val strictSettings = StrictScope.strictSettings
    val strictScalacOptions = StrictScope.strictScalacOptions
  }

  override def trigger: PluginTrigger = allRequirements

  override val projectSettings = StrictScope.defaultSettings
}

object StrictScope {
  private val fatalWarnings = "-Xfatal-warnings"

  val strictSettings = taskKey[Seq[Setting[_]]]("SBT settings to apply for the `strict` command")

  val strictScalacOptions = taskKey[Seq[String]]("Scalac options to apply for the `strict` command, removed outside the strict scope")

  val defaultSettings = Seq(
    strictSettings := defaultStrictSettings,
    strictScalacOptions := defaultStrictScalacOptions,
    commands += command,

    // Remove all strictScalacOptions from non-strict mode
    scalacOptions --= (strictScalacOptions.value),
  )

  // default values for the tasks and settings
  def defaultStrictSettings: Seq[Setting[_]] = Seq.empty

  def defaultStrictScalacOptions: Seq[String] = Seq(fatalWarnings)

  private def command = Command.single("strict") { (state0, scoped) =>
    val (_, extraSettings) = Project.extract(state0).runTask(strictSettings, state0)
    val (_, extraScalacOptions) = Project.extract(state0).runTask(strictScalacOptions, state0)
    val allStrictSettings: Seq[Setting[_]] = (scalacOptions ++= extraScalacOptions) :: extraSettings.toList
    val state3 = Project.extract(state0).appendWithoutSession(allStrictSettings, state0)
    val state4 = Command.process(scoped, state3)
    // Returning state3 makes the setting permanent, which we were trying to avoid.
    // Copying the `next` property appears to propagate failure correctly, but leave the
    // rest of the state alone.
    state0.copy(next=state4.next)
  }
}
