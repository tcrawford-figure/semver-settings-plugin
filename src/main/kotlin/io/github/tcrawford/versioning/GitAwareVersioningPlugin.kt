package io.github.tcrawford.versioning

import io.github.tcrawford.versioning.internal.calculator.VersionFactoryContext
import io.github.tcrawford.versioning.internal.calculator.versionFactory
import io.github.tcrawford.versioning.internal.extensions.providers
import io.github.tcrawford.versioning.internal.extensions.rootDir
import io.github.tcrawford.versioning.internal.logging.registerPostBuildVersionLogMessage
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

class GitAwareVersioningPlugin : Plugin<PluginAware> {
    override fun apply(target: PluginAware) {
        val semverExtension = SemverExtension(target)

        val versionFactoryContext = VersionFactoryContext(
            initialVersion = semverExtension.initialVersion.get(),
            stage = target.stage.get(),
            modifier = target.modifier.get(),
            forTesting = target.forTesting.get(),
            overrideVersion = target.overrideVersion.orNull,
            rootDir = semverExtension.rootProjectDir.getOrElse { target.rootDir }.asFile,
            mainBranch = semverExtension.mainBranch.orNull,
            developmentBranch = semverExtension.developmentBranch.orNull,
        )

        val nextVersion = target.providers.versionFactory(versionFactoryContext).get()

        target.registerPostBuildVersionLogMessage(nextVersion)
        target.writeVersionToPropertiesFile(nextVersion, target.tagPrefix.get())

        when (target) {
            is Settings -> target.gradle.beforeProject { project -> project.version = nextVersion }
            is Project -> target.version = nextVersion
            else -> error("Not a project or settings")
        }
    }
}
