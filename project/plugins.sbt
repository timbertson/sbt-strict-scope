// This project is its own plugin :)
unmanagedSourceDirectories in Compile += baseDirectory.value.getParentFile / "src" / "main" / "scala"

addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.13")
