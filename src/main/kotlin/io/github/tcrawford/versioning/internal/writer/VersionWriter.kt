package io.github.tcrawford.versioning.internal.writer

import io.github.tcrawford.versioning.Constants
import io.github.tcrawford.versioning.internal.extensions.getOrCreate
import io.github.tcrawford.versioning.internal.extensions.projectDir
import org.gradle.api.plugins.PluginAware

fun PluginAware.writeVersionToPropertiesFile(version: String, tagPrefix: String) {
    projectDir.resolve(Constants.SEMVER_PROPERTY_PATH).getOrCreate().writeText(
        """
        |version=$version
        |versionTag=$tagPrefix$version
        """.trimMargin(),
    )
}
