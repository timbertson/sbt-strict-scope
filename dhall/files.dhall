let Scala = ./dependencies/Scala.dhall
let Render = https://raw.githubusercontent.com/timbertson/dhall-render/5e2290a641c166c11d5eafb1eadb2a5bd5e83a01/package.dhall

in  { files = {=}
        -- [] : Render.File
        -- Scala.files
        --   Scala.Files::{
        --   , repo = "sbt-strict-scope"
        --   , scalaVersion = "2.12.15"
        --   , strictPluginOverride =
        --       -- This repo is the strict plugin, so we directly depend on our own source code
        --       Some
        --         ''
        --         Compile / unmanagedSourceDirectories += baseDirectory.value.getParentFile / "src" / "main" / "scala"
        --         ''
        --   , docker = Scala.Docker::{
        --     , updateRequires =
        --         Scala.Files.default.docker.updateRequires # [ "src" ]
        --     }
        --   }
    }
