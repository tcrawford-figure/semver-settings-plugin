package io.github.tcrawford.versioning.gradle

import io.github.tcrawford.versioning.kit.render.Element
import io.github.tcrawford.versioning.kit.render.Scribe

fun settingsGradle(
    fn: SettingsConfiguration.Builder.() -> Unit,
): SettingsConfiguration {
    val builder = SettingsConfiguration.Builder()
    builder.fn()
    return builder.build()
}

class SettingsConfiguration(
    private var buildCache: BuildCache? = null,
    private var semver: SemverConfiguration? = null,
) : Element.Line {
    override fun render(scribe: Scribe): String = buildString {
        semver?.let { sv ->
            append(scribe.use { s -> sv.render(s) })
            appendLine()
        }

        buildCache?.let { bc ->
            append(scribe.use { s -> bc.render(s) })
            appendLine()
        }
    }

    class Builder {
        var buildCache: BuildCache? = null
        var semver: SemverConfiguration? = null

        fun build(): SettingsConfiguration =
            SettingsConfiguration(
                buildCache = buildCache,
                semver = semver,
            )
    }
}
