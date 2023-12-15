package io.github.tcrawford.versioning.reader

import io.github.tcrawford.versioning.Constants
import java.io.File
import java.util.Properties

internal fun File.fetchVersion(): String =
    fetchSemverProperties().getProperty("version")

internal fun File.fetchVersionTag(): String =
    fetchSemverProperties().getProperty("versionTag")

private fun File.fetchSemverProperties(): Properties =
    resolve(Constants.SEMVER_PROPERTY_PATH).let {
        Properties().apply { load(it.reader()) }
    }
