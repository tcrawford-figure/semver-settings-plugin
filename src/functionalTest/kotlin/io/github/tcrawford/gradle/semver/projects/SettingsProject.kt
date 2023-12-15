package io.github.tcrawford.gradle.semver.projects

import com.autonomousapps.kit.GradleProject
import com.autonomousapps.kit.gradle.Plugins
import com.autonomousapps.kit.gradle.SettingsScript
import io.github.tcrawford.gradle.semver.plugins.GradlePlugins
import kotlin.io.path.createTempDirectory

class SettingsProject(
    override val projectName: String
) : AbstractProject() {
    override val gradleProject: GradleProject
        get() = build()

    private fun build(): GradleProject =
        newGradleProjectBuilder().withRootProject {
            val buildCacheDir = createTempDirectory("build-cache").toFile()
            buildCacheDir.deleteOnExit()

            settingsScript = SettingsScript(
                plugins = Plugins(GradlePlugins.gitAwareVersioningPlugin),
                additions = """
                    buildCache {
                      local {
                        directory = "${buildCacheDir.absolutePath}"
                      }
                    }
                """.trimIndent()
            )
            withBuildScript {
                plugins(GradlePlugins.kotlinNoApply)
            }
        }.write()
}
