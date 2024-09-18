package io.github.tcrawford.versioning.projects

import com.autonomousapps.kit.GradleProject
import com.autonomousapps.kit.gradle.SettingsScript
import io.github.tcrawford.versioning.gradle.SemverConfiguration
import io.github.tcrawford.versioning.gradle.buildCache
import io.github.tcrawford.versioning.gradle.local
import io.github.tcrawford.versioning.gradle.semver
import io.github.tcrawford.versioning.gradle.settingsGradle
import io.github.tcrawford.versioning.plugins.GradlePlugins

class RegularProject(
    override val projectName: String,
    private val semver: SemverConfiguration = semver {},
) : AbstractProject() {
    override val gradleProject: GradleProject
        get() = build()

    private fun build(): GradleProject =
        newGradleProjectBuilder(dslKind).withRootProject {
            withBuildScript {
                plugins(
                    GradlePlugins.gitAwareVersioningPlugin,
                    GradlePlugins.kotlinNoApply,
                )

                additions = scribe.use { s -> semver.render(s) }
            }

            val settings = settingsGradle {
                buildCache = buildCache {
                    local = local {
                        directory = buildCacheDir
                    }
                }
            }

            settingsScript = SettingsScript(
                additions = scribe.use { s -> settings.render(s) },
            )
        }.write()
}
