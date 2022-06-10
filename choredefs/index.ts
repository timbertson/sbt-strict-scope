import { defaultDockerOptions, default as Scala } from 'https://raw.githubusercontent.com/timbertson/chored-timbertson/16a85082768210d272aa784e236b59c9a4aa0d6b/lib/scala.ts#main'

export default Scala({ repo: 'sbt-strict-scope',
		scalaVersion: "2.12.15",
		strictPluginOverride:
			// This repo is the strict plugin, so we directly depend on our own source code
			`Compile / unmanagedSourceDirectories += baseDirectory.value.getParentFile / "src" / "main" / "scala"`,
		docker: {
			updateRequires: defaultDockerOptions.updateRequires.concat([ "src" ])
		},
})
