package io.github.tcrawford.gradle.semver.internal.writer

import io.github.tcrawford.gradle.semver.internal.extensions.getOrCreate
import org.gradle.api.initialization.Settings

internal fun Settings.writeVersionToPropertiesFile(version: String, tagPrefix: String) {
    settings.rootDir.resolve("build/reports/semver/semver.properties").getOrCreate()
        .writeText(
            """
                |version=$version
                |versionTag=$tagPrefix$version
            """.trimMargin()
        )
}
