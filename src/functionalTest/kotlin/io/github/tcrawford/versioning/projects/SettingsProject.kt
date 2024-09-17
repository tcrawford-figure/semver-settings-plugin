package io.github.tcrawford.versioning.projects

import com.autonomousapps.kit.GradleProject
import com.autonomousapps.kit.gradle.Plugins
import com.autonomousapps.kit.gradle.SettingsScript
import io.github.tcrawford.versioning.gradle.buildCache
import io.github.tcrawford.versioning.gradle.local
import io.github.tcrawford.versioning.gradle.settingsGradle
import io.github.tcrawford.versioning.gradle.toDirectory
import io.github.tcrawford.versioning.plugins.GradlePlugins

class SettingsProject(
    override val projectName: String,
) : AbstractProject() {
    override val gradleProject: GradleProject
        get() = build()

    private fun build(): GradleProject =
        newGradleProjectBuilder(dslKind).withRootProject {
            val settings = settingsGradle {
                buildCache = buildCache {
                    local = local {
                        directory = buildCacheDir.toDirectory()
                    }
                }
                // TODO: Support semverConfiguration when possible
            }

            settingsScript = SettingsScript(
                plugins = Plugins(GradlePlugins.gitAwareVersioningPlugin),
                additions = settings.render(scribe),
            )

            withBuildScript {
                plugins(GradlePlugins.kotlinNoApply)
            }
        }.write()
}
