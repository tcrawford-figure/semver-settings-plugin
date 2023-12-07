package io.github.tcrawford.gradle.semver.internal.writer

import io.github.tcrawford.gradle.semver.internal.extensions.getOrCreate
import io.github.tcrawford.gradle.semver.internal.extensions.rootDir
import org.gradle.api.plugins.PluginAware

internal fun PluginAware.writeVersionToPropertiesFile(version: String, tagPrefix: String) {
    rootDir.resolve("build/reports/semver/semver.properties").getOrCreate().writeText(
        """
        |version=$version
        |versionTag=$tagPrefix$version
        """.trimMargin()
    )
}
