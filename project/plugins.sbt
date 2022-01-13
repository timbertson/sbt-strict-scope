// This project is its own plugin :)
Compile / unmanagedSourceDirectories += baseDirectory.value.getParentFile / "src" / "main" / "scala"

addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.13")
