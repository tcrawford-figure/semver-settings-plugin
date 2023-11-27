package io.github.tcrawford.gradle.semver.reader

import java.io.File
import java.util.Properties

internal fun File.fetchVersion(): String =
    fetchSemverProperties().getProperty("version")

internal fun File.fetchVersionTag(): String =
    fetchSemverProperties().getProperty("versionTag")

private fun File.fetchSemverProperties(): Properties =
    resolve("build/reports/semver/semver.properties").let {
        Properties().apply { load(it.reader()) }
    }
