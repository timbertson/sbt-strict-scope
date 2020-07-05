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

The idea is that you can leave off `-Xfatal-warnings` in your main build, and in CI (or before you commit) you run `sbt 'strict test'` (note the quotes: you're passing the `"test"` argument into the `strict` command, you're not running `strict` followed by `test`). That'll run the `test` command, but with your strict settings enabled.

### `sbt-tpolecat` users:

If you're using this plugin (I am), you need to fight it a little. It enables `-Xfatal-warnings` by default already, so in your project you'll need to add:

```scala
scalacOptions ~= (_ filterNot (_ == "-Xfatal-warnings"))
```
