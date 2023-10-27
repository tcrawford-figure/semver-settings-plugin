package com.figure.gradle.semver.internal

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import java.io.File
import java.util.Properties

internal val Project.gradlePropertiesFile: File
    get() = projectDir.resolve("gradle.properties")

internal val Project.gradleProperties: Properties
    get() = Properties().apply { load(gradlePropertiesFile.inputStream()) }

internal val Project.modifierProperty: Provider<String>
    get() = semverProperty(SemverProperty.Modifier).orElse(Modifier.Auto.value)

internal val Project.stageProperty: Provider<String>
    get() = semverProperty(SemverProperty.Stage).orElse(Stage.Stable.value)

internal val Project.tagPrefixProperty: Provider<String>
    get() = semverProperty(SemverProperty.TagPrefix).orElse("v")

internal val Project.overrideVersion: Provider<String>
    get() = semverProperty(SemverProperty.OverrideVersion)

internal enum class SemverProperty(val property: String) {
    Stage("semver.stage"),
    Modifier("semver.modifier"),
    TagPrefix("semver.tagPrefix"),
    OverrideVersion("semver.overrideVersion"),
}

internal enum class Modifier(val value: String) {
    Major("major"),
    Minor("minor"),
    Patch("patch"),
    Auto("auto"),
}

// In order from lowest to highest priority
internal enum class Stage(val value: String) {
    Dev("dev"),
    Alpha("alpha"),
    Beta("beta"),
    Preview("rc"),
    Snapshot("snapshot"),
    Final("final"),
    GA("ga"),
    Release("release"),
    Stable("stable"),
}

private fun Project.semverProperty(semverProperty: SemverProperty): Provider<String> =
    when {
        gradle.startParameter.projectProperties[semverProperty.property] != null -> {
            provider { gradle.startParameter.projectProperties[semverProperty.property] }
        }

        gradlePropertiesFile.exists() -> {
            provider { gradleProperties.getProperty(semverProperty.property) }
        }

        else -> {
            providers.gradleProperty(semverProperty.property)
        }
    }
