import { defaultDockerOptions, default as Scala } from 'https://raw.githubusercontent.com/timbertson/chored-timbertson/219bc74bb3fdeccb1aba7122e729d211a858061e/lib/scala.ts#main'

export default Scala({ repo: 'sbt-strict-scope',
		scala2Version: "2.12.15",
		strictPluginOverride:
			// This repo is the strict plugin, so we directly depend on our own source code
			`Compile / unmanagedSourceDirectories += baseDirectory.value.getParentFile / "src" / "main" / "scala"`,
		docker: {
			updateRequires: defaultDockerOptions.updateRequires.concat([ "src" ])
		},
})
