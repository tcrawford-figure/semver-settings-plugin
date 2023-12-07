package io.github.tcrawford.gradle.semver.internal.properties

import io.github.tcrawford.gradle.semver.internal.extensions.gradle
import io.github.tcrawford.gradle.semver.internal.extensions.providers
import org.gradle.api.plugins.PluginAware
import org.gradle.api.provider.Provider

internal val PluginAware.modifier: Provider<Modifier>
    get() = semverProperty(SemverProperty.Modifier).map { Modifier.fromValue(it) }.orElse(Modifier.Auto)

internal val PluginAware.stage: Provider<Stage>
    get() = semverProperty(SemverProperty.Stage).map { Stage.fromValue(it) }.orElse(Stage.Auto)

internal val PluginAware.tagPrefix: Provider<String>
    get() = semverProperty(SemverProperty.TagPrefix).orElse("v")

internal val PluginAware.overrideVersion: Provider<String>
    get() = semverProperty(SemverProperty.OverrideVersion)

internal val PluginAware.forTesting: Provider<Boolean>
    get() = semverProperty(SemverProperty.ForTesting).map { it.toBoolean() }.orElse(false)

private fun PluginAware.semverProperty(semverProperty: SemverProperty): Provider<String> =
    providers.provider { gradle.startParameter.projectProperties[semverProperty.property] }
