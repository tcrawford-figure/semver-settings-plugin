package io.github.tcrawford.gradle.semver.internal.writer

import io.github.tcrawford.gradle.semver.Constants
import io.github.tcrawford.gradle.semver.internal.extensions.getOrCreate
import io.github.tcrawford.gradle.semver.internal.extensions.projectDir
import org.gradle.api.plugins.PluginAware

fun PluginAware.writeVersionToPropertiesFile(version: String, tagPrefix: String) {
    projectDir.resolve(Constants.SEMVER_PROPERTY_PATH).getOrCreate().writeText(
        """
        |version=$version
        |versionTag=$tagPrefix$version
        """.trimMargin(),
    )
}
