package io.github.tcrawford.versioning.internal.properties

import io.github.tcrawford.versioning.internal.extensions.gradle
import io.github.tcrawford.versioning.internal.extensions.providers
import org.gradle.api.plugins.PluginAware
import org.gradle.api.provider.Provider

val PluginAware.modifier: Provider<Modifier>
    get() = semverProperty(SemverProperty.Modifier).map { Modifier.fromValue(it) }.orElse(Modifier.Auto)

val PluginAware.stage: Provider<Stage>
    get() = semverProperty(SemverProperty.Stage).map { Stage.fromValue(it) }.orElse(Stage.Auto)

val PluginAware.tagPrefix: Provider<String>
    get() = semverProperty(SemverProperty.TagPrefix).orElse("v")

val PluginAware.overrideVersion: Provider<String>
    get() = semverProperty(SemverProperty.OverrideVersion)

val PluginAware.forTesting: Provider<Boolean>
    get() = semverProperty(SemverProperty.ForTesting).map { it.toBoolean() }.orElse(false)

private fun PluginAware.semverProperty(semverProperty: SemverProperty): Provider<String> =
    providers.provider { gradle.startParameter.projectProperties[semverProperty.property] }
