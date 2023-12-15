package io.github.tcrawford.versioning.projects

import com.autonomousapps.kit.GradleProject
import com.autonomousapps.kit.gradle.SettingsScript
import io.github.tcrawford.versioning.plugins.GradlePlugins

class RegularProject(
    override val projectName: String,
) : AbstractProject() {
    override val gradleProject: GradleProject
        get() = build()

    private fun build(): GradleProject =
        newGradleProjectBuilder().withRootProject {
            withBuildScript {
                plugins(
                    GradlePlugins.gitAwareVersioningPlugin,
                    GradlePlugins.kotlinNoApply,
                )
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
        }.write()
}
