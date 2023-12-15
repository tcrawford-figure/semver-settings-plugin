package io.github.tcrawford.gradle.semver.projects

import com.autonomousapps.kit.GradleProject
import com.autonomousapps.kit.gradle.SettingsScript
import io.github.tcrawford.gradle.semver.plugins.GradlePlugins
import io.github.tcrawford.gradle.semver.utils.registerForCleanup
import kotlin.io.path.createTempDirectory

class RegularProject(
    override val projectName: String
) : AbstractProject() {
    override val gradleProject: GradleProject
        get() = build()

    private fun build(): GradleProject =
        newGradleProjectBuilder().withRootProject {
            withBuildScript {
                plugins(
                    GradlePlugins.gitAwareVersioningPlugin,
                    GradlePlugins.kotlinNoApply
                )
            }

            val buildCacheDir = createTempDirectory("build-cache").toFile().registerForCleanup()

            settingsScript = SettingsScript(
                additions = """
                    buildCache {
                        local {
                            directory = "${buildCacheDir.absolutePath}"
                        }
                    }
                """.trimIndent()
            )
        }.write()
}
