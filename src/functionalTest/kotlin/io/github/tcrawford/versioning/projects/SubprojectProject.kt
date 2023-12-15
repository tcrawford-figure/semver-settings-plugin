package io.github.tcrawford.versioning.projects

import com.autonomousapps.kit.GradleProject
import com.autonomousapps.kit.gradle.SettingsScript
import io.github.tcrawford.versioning.Constants
import io.github.tcrawford.versioning.plugins.GradlePlugins
import java.util.Properties

class SubprojectProject(
    override val projectName: String,
) : AbstractProject() {
    override val gradleProject: GradleProject
        get() = build()

    private val subprojectName = "subproj"

    private fun build(): GradleProject =
        newGradleProjectBuilder().withRootProject {
            withBuildScript {
                plugins(GradlePlugins.kotlinNoApply)
            }

            settingsScript = SettingsScript(
                additions = """
                    buildCache {
                        local {
                            directory = "${buildCacheDir.absolutePath}"
                        }
                    }
                """.trimIndent(),
            )
        }.withSubproject(subprojectName) {
            withBuildScript {
                plugins(GradlePlugins.gitAwareVersioningPlugin, GradlePlugins.kotlinNoApply)
            }
        }.write()

    override fun fetchSemverProperties(): Properties =
        gradleProject.projectDir(subprojectName).toFile().resolve(Constants.SEMVER_PROPERTY_PATH).let {
            Properties().apply { load(it.reader()) }
        }
}
