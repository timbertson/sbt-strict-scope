import { defaultDockerOptions, default as Scala } from 'https://raw.githubusercontent.com/timbertson/chored-timbertson/b81b414a6bc1b3fe50fc455a10695d0129af7cbb/lib/scala.ts#main'

export default Scala({ repo: 'sbt-strict-scope',
		scalaVersion: "2.12.15",
		strictPluginOverride:
			// This repo is the strict plugin, so we directly depend on our own source code
			`Compile / unmanagedSourceDirectories += baseDirectory.value.getParentFile / "src" / "main" / "scala"`,
		docker: {
			updateRequires: defaultDockerOptions.updateRequires.concat([ "src" ])
		},
})
