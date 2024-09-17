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
) : Element.Line {
    override fun render(scribe: Scribe): String = buildString {
        buildCache?.let { bc ->
            appendLine(scribe.use { s -> bc.render(s) })
        }
    }

    class Builder {
        var buildCache: BuildCache? = null

        fun build(): SettingsConfiguration =
            SettingsConfiguration(
                buildCache = buildCache,
            )
    }
}
