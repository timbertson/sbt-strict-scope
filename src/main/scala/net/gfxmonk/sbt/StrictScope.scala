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

  private def command = Command.single("strict") { (stateOriginal, scoped) =>
    val (_, extraSettings) = Project.extract(stateOriginal).runTask(strictSettings, stateOriginal)
    val (_, extraScalacOptions) = Project.extract(stateOriginal).runTask(strictScalacOptions, stateOriginal)
    val allStrictSettings: Seq[Setting[_]] = (scalacOptions ++= extraScalacOptions) :: extraSettings.toList

    val stateWithSettings = Project.extract(stateOriginal).appendWithoutSession(allStrictSettings, stateOriginal)
    val stateAfterCommand = Command.process(scoped, stateWithSettings)
    // Return the final state, but with the original attributes (i.e. no altered settings)
    stateAfterCommand.copy(attributes = stateOriginal.attributes)
  }
}
