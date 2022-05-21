<img src="http://gfxmonk.net/dist/status/project/sbt-strict-scope.png">

# Strict Scope (for SBT):

Adds a `strict` command to run whatever you like with stricter settings.

## Motivation:

`-Xfatal-warnings` is a great flag. Love it.

Also, I hate it so much. When I'm debugging, sometimes I just want to Do Things like comment out a bit of code for a minute, without fixing up my imports or getting rid of unused arguments. Just quit whining for a minute would you? I promise I'll fix it all up before I commit. I just need a moment alone with the compiler.

## Usage:

This plugin is simple. It adds a `strict` command which runs a subcommand with `strictSettings` applied. By default, those are `scalacOptions += "-Xfatal-warnings"` but you change that to whatever you like, I'm not your boss.

```scala
// plugins.sbt:
addSbtPlugin("net.gfxmonk" % "sbt-strict-scope" % "LATEST_VERSION")
```

(see [releases](https://github.com/timbertson/sbt-strict-scope/releases) for available versions)

The idea is that you can leave off `-Xfatal-warnings` in your main build, and in CI (or before you commit) you run `sbt 'strict test'` (note the quotes: you're passing the `"test"` argument into the `strict` command, you're not running `strict` followed by `test`). That'll run the `test` command, but with your strict settings enabled.

### Customization:

There are two settings to control what happens in `strict` scope:

 - `strictSettings: taskKey[Seq[Setting[_]]]` - SBT settings to apply for the `strict` command
 - `strictScalacOptions: taskKey[Seq[String]]` - Scalac options to apply for the `strict` command, removed outside the strict scope

By default `strictSettings` is empty, and `strictScalacOptions` contains only `-Xfatal-warnings`.

**Note**: As of version 2.0.0, this plugin _removes_ `strictScalacOptions` from `scalacOptions` outside of strict mode. This is convenient when using e.g. `sbt-tpolecat`, which enables `-Xfatal-warnings`. You'll still get all of its other flags, but `-Xfatal-warnings` will only apply in `strict` mode.
