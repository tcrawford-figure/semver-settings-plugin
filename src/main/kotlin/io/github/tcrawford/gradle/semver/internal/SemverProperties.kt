package io.github.tcrawford.gradle.semver.internal

import io.github.z4kn4fein.semver.Inc
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.toVersion
import org.gradle.api.initialization.Settings
import org.gradle.api.provider.Provider
import java.io.File
import java.util.Properties

internal val Settings.gradlePropertiesFile: File
    get() = settingsDir.resolve("gradle.properties")

internal val Settings.gradleProperties: Properties
    get() = Properties().apply { load(gradlePropertiesFile.inputStream()) }

// TODO: Add error handling for `fromValue` when bad input is provided
internal val Settings.modifier: Provider<Modifier>
    get() = semverProperty(SemverProperty.Modifier).map { Modifier.fromValue(it) }.orElse(Modifier.Auto)

// TODO: Add error handling for `fromValue` when bad input is provided
internal val Settings.stage: Provider<Stage>
    get() = semverProperty(SemverProperty.Stage).map { Stage.fromValue(it) }.orElse(Stage.Auto)

internal val Settings.tagPrefix: Provider<String>
    get() = semverProperty(SemverProperty.TagPrefix).orElse("v")

internal val Settings.overrideVersion: Provider<Version>
    get() = semverProperty(SemverProperty.OverrideVersion).map { it.toVersion() }

internal val Settings.forTesting: Provider<Boolean>
    get() = semverProperty(SemverProperty.ForTesting).map { it.toBoolean() }.orElse(false)

internal enum class SemverProperty(val property: String) {
    Stage("semver.stage"),
    Modifier("semver.modifier"),
    TagPrefix("semver.tagPrefix"),
    OverrideVersion("semver.overrideVersion"),

    ForTesting("semver.forTesting")
}

internal enum class Modifier(val value: String) {
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
                ?: error("Invalid modifier provided: $value")
    }
}

// In order from lowest to highest priority
internal enum class Stage(val value: String) {
    Dev("dev"),
    Alpha("alpha"),
    Beta("beta"),
    RC("rc"),
    Snapshot("SNAPSHOT"),
    Final("final"),
    GA("ga"),
    Release("release"),
    Stable("stable"),
    Auto("auto");

    companion object {
        fun fromValue(value: String): Stage =
            entries.find { it.value.lowercase() == value.lowercase() }
                ?: error("Invalid stage provided: $value")
    }
}

private fun Settings.semverProperty(semverProperty: SemverProperty): Provider<String> =
    when {
        gradle.startParameter.projectProperties[semverProperty.property] != null -> {
            providers.provider { gradle.startParameter.projectProperties[semverProperty.property] }
        }

        gradlePropertiesFile.exists() -> {
            providers.provider { gradleProperties.getProperty(semverProperty.property) }
        }

        else -> {
            providers.gradleProperty(semverProperty.property)
        }
    }
