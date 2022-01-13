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

  private case class ProjectScopes(projectScope: Scope, configurationScopes: List[Scope])

  private def setScope(scope: Scope): ScopedKey ~> ScopedKey = new ~>[ScopedKey, ScopedKey] {
    override def apply[T](key: sbt.ScopedKey[T]): sbt.ScopedKey[T] = key.copy(scope = scope)
  }

  private def command = Command.single("strict") { (stateOriginal, command) =>
    // "strict" builds a parallel universe, where all projects in all configurations have
    // that project's strict settings enabled.
    // It then runs the given command in that universe. This ensures that dependent tasks
    // (via dependsOn / aggregates) are evaluated in strict mode, too.

    val extracted = Project.extract(stateOriginal)

    // Enumerate all possible project/config scopes
    val allScopes: List[ProjectScopes] = extracted.structure.units.toList.flatMap { case (uri, unit) =>
      unit.defined.map { case (key, proj) =>
        val selectProject = Select(ProjectRef(uri, key))
        ProjectScopes(
          projectScope = Scope(selectProject, Zero, Zero, Zero),
          configurationScopes = proj.configurations.map { config =>
            Scope(selectProject, Select(config), Zero, Zero)
          }.toList
        )
      }
    }

    val allStrictSettings: Seq[Setting[_]] = allScopes.flatMap { scopes =>
      // extract extraSettings / extraScalacOptions values from this project
      // (we assume they don't differ per-config)
      val (_, extraScalacOptions) = extracted.runTask(strictScalacOptions.in(scopes.projectScope), stateOriginal)
      val (_, extraSettings) = extracted.runTask(strictSettings.in(scopes.projectScope), stateOriginal)

      // Generate the vanilla settings type
      val newSettings = (scalacOptions ++= extraScalacOptions) :: extraSettings.toList

      // And then apply these settings in _every_ configuration scope in this project,
      // as well as the Zero-configuration project scope.
      //
      // Config scopes are required so that we override e.g. Config / scalacOptions.
      // But project-scopes appear to be necessary required for normal tasks
      // (i.e. a plain `taskKey` which doesn't care about the configuration axis).
      //
      // Most of these scopes will never be referenced, but it's harmless :shrug:
      //
      // (I desperately hope I never have to learn more about the remaining two
      // axes that are untouched here)
      (scopes.projectScope :: scopes.configurationScopes).flatMap { configScope =>
        newSettings.map(_.mapKey(setScope(configScope)))
      }
    }

    val stateWithSettings = extracted.appendWithoutSession(allStrictSettings, stateOriginal)
    val stateAfterCommand = Command.process(command, stateWithSettings)
    // Return the final state, but with the original attributes (i.e. no altered settings)
    stateAfterCommand.copy(
      attributes = stateOriginal.attributes,
    )
  }
}
