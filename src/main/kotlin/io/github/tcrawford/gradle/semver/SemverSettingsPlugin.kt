package io.github.tcrawford.gradle.semver

import io.github.tcrawford.gradle.semver.internal.calculator.versionFactory
import io.github.tcrawford.gradle.semver.internal.extensions.lifecycle
import io.github.tcrawford.gradle.semver.internal.forTesting
import io.github.tcrawford.gradle.semver.internal.modifier
import io.github.tcrawford.gradle.semver.internal.overrideVersion
import io.github.tcrawford.gradle.semver.internal.stage
import io.github.tcrawford.gradle.semver.internal.tagPrefix
import io.github.tcrawford.gradle.semver.internal.writer.writeVersion
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

private val log: Logger = Logging.getLogger(Logger.ROOT_LOGGER_NAME)

class SemverSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        val semverExtension = SemverExtension(settings)

        // Don't inline this. The `convention` lambda doesn't like to serialize the settings
        // object and evaluate the value later.
        val settingsDir = settings.rootDir

        val nextVersion = settings.providers.versionFactory(
            initialVersion = semverExtension.initialVersion,
            stage = settings.stage,
            modifier = settings.modifier,
            forTesting = settings.forTesting,
            // TODO: Check error handling on this if version is invalid
            overrideVersion = settings.overrideVersion,
            rootDir = semverExtension.rootProjectDir.convention { settingsDir },
            mainBranch = semverExtension.mainBranch,
            developmentBranch = semverExtension.developmentBranch,
        ).get()

        // TODO: Log this at the end of the build
        log.lifecycle { nextVersion }
        settings.writeVersion(nextVersion, settings.tagPrefix.get())

        settings.gradle.beforeProject { project ->
            project.version = nextVersion
        }
    }
}
