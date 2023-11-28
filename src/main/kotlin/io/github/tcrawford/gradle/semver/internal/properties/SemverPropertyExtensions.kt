package io.github.tcrawford.gradle.semver.internal.properties

import org.gradle.api.initialization.Settings
import org.gradle.api.provider.Provider
import java.io.File
import java.util.Properties

internal val Settings.gradlePropertiesFile: File
    get() = settingsDir.resolve("gradle.properties")

internal val Settings.gradleProperties: Properties
    get() = Properties().apply { load(gradlePropertiesFile.inputStream()) }

internal val Settings.modifier: Provider<Modifier>
    get() = semverProperty(SemverProperty.Modifier).map { Modifier.fromValue(it) }.orElse(Modifier.Auto)

internal val Settings.stage: Provider<Stage>
    get() = semverProperty(SemverProperty.Stage).map { Stage.fromValue(it) }.orElse(Stage.Auto)

internal val Settings.tagPrefix: Provider<String>
    get() = semverProperty(SemverProperty.TagPrefix).orElse("v")

internal val Settings.overrideVersion: Provider<String>
    get() = semverProperty(SemverProperty.OverrideVersion)

internal val Settings.forTesting: Provider<Boolean>
    get() = semverProperty(SemverProperty.ForTesting).map { it.toBoolean() }.orElse(false)

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
