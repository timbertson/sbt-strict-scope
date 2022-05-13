import { defaultDockerOptions, default as Scala } from 'https://raw.githubusercontent.com/timbertson/chored-timbertson/09153fae3d365ce66bb9e4f4505e50da9e83a290/lib/scala.ts#main'

export default Scala({ repo: 'sbt-strict-scope',
		scalaVersion: "2.12.15",
		strictPluginOverride:
			// This repo is the strict plugin, so we directly depend on our own source code
			`Compile / unmanagedSourceDirectories += baseDirectory.value.getParentFile / "src" / "main" / "scala"`,
		docker: {
			updateRequires: defaultDockerOptions.updateRequires.concat([ "src" ])
		},
})
