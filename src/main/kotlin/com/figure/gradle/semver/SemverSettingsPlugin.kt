package com.figure.gradle.semver

import com.figure.gradle.semver.internal.calculator.VersionFactory
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.kotlin.dsl.create

internal val log: Logger = Logging.getLogger(Logger.ROOT_LOGGER_NAME)

class SemverSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {

        settings.gradle.rootProject { rootProject ->
            val semverExtension = settings.extensions.create<SemverExtension>("semver", rootProject.objects, settings)

            val nextVersion = VersionFactory.nextVersion(semverExtension, rootProject)
            log.lifecycle("Found next version: $nextVersion")

            settings.gradle.beforeProject { project ->
                project.version = nextVersion
            }
        }
    }
}
