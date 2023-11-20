package io.github.tcrawford.gradle.semver.reader

import java.io.File
import java.util.Properties

internal fun File.fetchVersion(): String {
    val semverPropertiesFile = resolve("build/reports/semver/semver.properties")
    val semverProperties = Properties().apply { load(semverPropertiesFile.reader()) }
    return semverProperties.getProperty("version")
}
