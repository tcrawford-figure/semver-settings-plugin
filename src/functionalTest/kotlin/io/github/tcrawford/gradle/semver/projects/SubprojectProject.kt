package io.github.tcrawford.gradle.semver.projects

import com.autonomousapps.kit.GradleProject
import com.autonomousapps.kit.gradle.SettingsScript
import io.github.tcrawford.gradle.semver.Constants
import io.github.tcrawford.gradle.semver.plugins.GradlePlugins
import java.util.Properties
import kotlin.io.path.createTempDirectory

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

            val buildCacheDir = createTempDirectory("build-cache").toFile()
            buildCacheDir.deleteOnExit()

            settingsScript = SettingsScript(
                additions = """
                    buildCache {
                        local {
                            directory = "${buildCacheDir.absolutePath}"
                        }
                    }
                """.trimIndent()
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
