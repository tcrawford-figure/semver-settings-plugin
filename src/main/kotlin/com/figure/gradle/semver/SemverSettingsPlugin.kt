package com.figure.gradle.semver

import com.figure.gradle.semver.internal.SemverContext
import com.figure.gradle.semver.internal.calculator.StableVersionCalculator
import com.figure.gradle.semver.internal.modifierProperty
import com.figure.gradle.semver.internal.stageProperty
import com.figure.gradle.semver.internal.tagPrefixProperty
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

internal val log: Logger = Logging.getLogger(Logger.ROOT_LOGGER_NAME)

class SemverSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        settings.gradle.rootProject {
            log.lifecycle("Doing something before a project")
            log.lifecycle("Found stage: ${it.stageProperty.get()}")
            log.lifecycle("Found modifier: ${it.modifierProperty.get()}")
            log.lifecycle("Found tag prefix: ${it.tagPrefixProperty.get()}")

            val versionCalculator = StableVersionCalculator()
            val semverContext = SemverContext(it.stageProperty.get(), it.modifierProperty.get())

            val stableVersion = versionCalculator.calculate(semverContext, it)
            log.lifecycle("Found next stable version: $stableVersion")
        }

        settings.gradle.beforeProject {
            it.version = "0.1.0"
        }
    }
}
