package com.figure.gradle.semver

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
        settings.gradle.beforeProject {
            log.info("Found stage: ${it.stageProperty.get()}")
            log.info("Found modifier: ${it.modifierProperty.get()}")
            log.info("Found tag prefix: ${it.tagPrefixProperty.get()}")

            it.version = "0.1.0"
        }
    }
}
