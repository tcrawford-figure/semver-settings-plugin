package io.github.tcrawford.gradle.semver.internal.properties

import io.github.z4kn4fein.semver.Inc

enum class Modifier(val value: String) {
    Major("major"),
    Minor("minor"),
    Patch("patch"),
    Auto("auto");

    fun toInc(): Inc = when (this) {
        Major -> Inc.MAJOR
        Minor -> Inc.MINOR
        Patch -> Inc.PATCH
        Auto -> Inc.PATCH
    }

    companion object {
        fun fromValue(value: String): Modifier =
            entries.find { it.value.lowercase() == value.lowercase() }
                ?: error("Invalid modifier provided: $value. Valid values are: ${entries.joinToString { it.value }}")
    }
}
