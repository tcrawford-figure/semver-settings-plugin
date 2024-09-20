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
package io.github.tcrawford.semver.projects

import com.autonomousapps.kit.GradleProject
import com.autonomousapps.kit.gradle.Plugins
import com.autonomousapps.kit.gradle.SettingsScript
import io.github.tcrawford.semver.gradle.SemverConfiguration
import io.github.tcrawford.semver.gradle.buildCache
import io.github.tcrawford.semver.gradle.local
import io.github.tcrawford.semver.gradle.semver
import io.github.tcrawford.semver.gradle.settingsGradle
import io.github.tcrawford.semver.plugins.GradlePlugins

class SettingsProject(
    override val projectName: String,
    private val semver: SemverConfiguration = semver { },
) : AbstractProject() {
    override val gradleProject: GradleProject
        get() = build()

    private fun build(): GradleProject =
        newGradleProjectBuilder(dslKind).withRootProject {
            val settings = settingsGradle {
                buildCache = buildCache {
                    local = local {
                        directory = buildCacheDir
                    }
                }
                semver = this@SettingsProject.semver
            }

            settingsScript = SettingsScript(
                plugins = Plugins(GradlePlugins.semverPlugin),
                additions = scribe.use { s -> settings.render(s) },
            )

            withBuildScript {
                plugins(GradlePlugins.kotlinNoApply)
            }
        }.write()
}
