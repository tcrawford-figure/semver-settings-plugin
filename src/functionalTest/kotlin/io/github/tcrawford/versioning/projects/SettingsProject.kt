package io.github.tcrawford.versioning.projects

import com.autonomousapps.kit.GradleProject
import com.autonomousapps.kit.gradle.Plugins
import com.autonomousapps.kit.gradle.SettingsScript
import io.github.tcrawford.versioning.plugins.GradlePlugins

class SettingsProject(
    override val projectName: String,
) : AbstractProject() {
    override val gradleProject: GradleProject
        get() = build()

    private fun build(): GradleProject =
        newGradleProjectBuilder().withRootProject {
            settingsScript = SettingsScript(
                plugins = Plugins(GradlePlugins.gitAwareVersioningPlugin),
                additions = """
                    buildCache {
                      local {
                        directory = "${buildCacheDir.absolutePath}"
                      }
                    }
                """.trimIndent(),
            )
            withBuildScript {
                plugins(GradlePlugins.kotlinNoApply)
            }
        }.write()
}
