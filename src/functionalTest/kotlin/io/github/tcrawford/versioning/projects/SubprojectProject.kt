package io.github.tcrawford.versioning.projects

import com.autonomousapps.kit.GradleProject
import com.autonomousapps.kit.gradle.SettingsScript
import io.github.tcrawford.versioning.Constants
import io.github.tcrawford.versioning.gradle.SemverConfiguration
import io.github.tcrawford.versioning.gradle.buildCache
import io.github.tcrawford.versioning.gradle.local
import io.github.tcrawford.versioning.gradle.semver
import io.github.tcrawford.versioning.gradle.settingsGradle
import io.github.tcrawford.versioning.plugins.GradlePlugins
import java.util.Properties

class SubprojectProject(
    override val projectName: String,
    private val semver: SemverConfiguration = semver {},
) : AbstractProject() {
    override val gradleProject: GradleProject
        get() = build()

    private val subprojectName = "subproj"

    private fun build(): GradleProject =
        newGradleProjectBuilder(dslKind).withRootProject {
            withBuildScript {
                plugins(GradlePlugins.kotlinNoApply)
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
        }.withSubproject(subprojectName) {
            withBuildScript {
                plugins(GradlePlugins.gitAwareVersioningPlugin, GradlePlugins.kotlinNoApply)
                additions = scribe.use { s -> semver.render(s) }
            }
        }.write()

    override fun fetchSemverProperties(): Properties =
        gradleProject.projectDir(subprojectName).toFile().resolve(Constants.SEMVER_PROPERTY_PATH).let {
            Properties().apply { load(it.reader()) }
        }
}
