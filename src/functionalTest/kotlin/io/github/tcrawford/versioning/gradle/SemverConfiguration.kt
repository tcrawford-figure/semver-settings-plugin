package io.github.tcrawford.versioning.gradle

import io.github.tcrawford.versioning.kit.render.Element
import io.github.tcrawford.versioning.kit.render.Scribe
import java.io.File

fun semver(
    fn: SemverConfiguration.Builder.() -> Unit,
): SemverConfiguration {
    val builder = SemverConfiguration.Builder()
    builder.fn()
    return builder.build()
}

class SemverConfiguration(
    val rootProjectDir: File? = null,
    val initialVersion: String?,
    val mainBranch: String?,
    val developmentBranch: String?,
) : Element.Block {
    override val name: String = "semver"

    override fun render(scribe: Scribe): String = scribe.block(this) { s ->
        rootProjectDir?.let {
            s.line { s.append("rootProjectDir = \"$rootProjectDir\"") }
        }
        initialVersion?.let {
            s.line { s.append("initialVersion = \"$initialVersion\"") }
        }
        mainBranch?.let {
            s.line { s.append("mainBranch = \"$mainBranch\"") }
        }
        developmentBranch?.let {
            s.line { s.append("developmentBranch = \"$developmentBranch\"") }
        }
    }

    class Builder {
        var rootProjectDir: File? = null
        var initialVersion: String? = null
        var mainBranch: String? = null
        var developmentBranch: String? = null

        fun build(): SemverConfiguration =
            SemverConfiguration(
                rootProjectDir = rootProjectDir,
                initialVersion = initialVersion,
                mainBranch = mainBranch,
                developmentBranch = developmentBranch,
            )
    }
}
