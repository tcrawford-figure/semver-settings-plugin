/*
 * Copyright (C) 2024 Tyler Crawford
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

val PluginAware.forMajorVersion: Provider<Int>
    get() = semverProperty(SemverProperty.ForMajorVersion).map {
        runCatching {
            it.toInt()
        }.getOrElse {
            error("semver.forMajorVersion must be representative of a valid major version line (0, 1, 2, etc.)")
        }
    }

val PluginAware.appendBuildMetadata: Provider<String>
    get() = semverProperty(SemverProperty.AppendBuildMetadata)

val PluginAware.forTesting: Provider<Boolean>
    get() = semverProperty(SemverProperty.ForTesting).map { it.toBoolean() }.orElse(false)

private fun PluginAware.semverProperty(semverProperty: SemverProperty): Provider<String> =
    providers.provider { gradle.startParameter.projectProperties[semverProperty.property] }
