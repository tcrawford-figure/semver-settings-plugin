package io.github.tcrawford.gradle.semver.internal.writer

import io.github.tcrawford.gradle.semver.internal.extensions.getOrCreate
import org.gradle.api.initialization.Settings

internal fun Settings.writeVersion(version: String) {
    settings.rootDir.resolve("build/reports/semver/semver.properties").getOrCreate().writeText("version=$version")
}
