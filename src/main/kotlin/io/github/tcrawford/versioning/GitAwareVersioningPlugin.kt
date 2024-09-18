package io.github.tcrawford.versioning

import io.github.tcrawford.versioning.internal.calculator.VersionFactoryContext
import io.github.tcrawford.versioning.internal.calculator.versionFactory
import io.github.tcrawford.versioning.internal.extensions.extensions
import io.github.tcrawford.versioning.internal.extensions.providers
import io.github.tcrawford.versioning.internal.extensions.rootDir
import io.github.tcrawford.versioning.internal.logging.registerPostBuildVersionLogMessage
import io.github.tcrawford.versioning.internal.properties.forMajorVersion
import io.github.tcrawford.versioning.internal.properties.forTesting
import io.github.tcrawford.versioning.internal.properties.modifier
import io.github.tcrawford.versioning.internal.properties.overrideVersion
import io.github.tcrawford.versioning.internal.properties.stage
import io.github.tcrawford.versioning.internal.properties.tagPrefix
import io.github.tcrawford.versioning.internal.writer.writeVersionToPropertiesFile
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.PluginAware
import org.gradle.kotlin.dsl.create

class GitAwareVersioningPlugin : Plugin<PluginAware> {
    override fun apply(target: PluginAware) {
        val semverExtension = target.extensions.create<SemverExtension>("semver").apply {
            initialVersion.convention("0.0.0")
        }

        when (target) {
            is Settings -> {
                target.gradle.beforeProject {
                    val nextVersion = target.calculateVersion(semverExtension)
                    it.version = nextVersion
                }
            }

            is Project -> {
                target.afterEvaluate {
                    val nextVersion = target.calculateVersion(semverExtension)
                    target.version = nextVersion
                }
            }

            else -> error("Not a project or settings")
        }
    }

    private fun PluginAware.calculateVersion(semverExtension: SemverExtension): String {
        val versionFactoryContext = VersionFactoryContext(
            initialVersion = semverExtension.initialVersion.get(),
            stage = this.stage.get(),
            modifier = this.modifier.get(),
            forTesting = this.forTesting.get(),
            overrideVersion = this.overrideVersion.orNull,
            forMajorVersion = this.forMajorVersion.orNull,
            rootDir = semverExtension.rootProjectDir.getOrElse { this.rootDir }.asFile,
            mainBranch = semverExtension.mainBranch.orNull,
            developmentBranch = semverExtension.developmentBranch.orNull,
        )

        val nextVersion = this.providers.versionFactory(versionFactoryContext).get()

        this.registerPostBuildVersionLogMessage(nextVersion)
        this.writeVersionToPropertiesFile(nextVersion, tagPrefix.get())

        return nextVersion
    }
}
