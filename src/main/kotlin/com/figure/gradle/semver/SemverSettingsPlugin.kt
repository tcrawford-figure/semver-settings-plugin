package com.figure.gradle.semver

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

internal val log: Logger = Logging.getLogger(Logger.ROOT_LOGGER_NAME)

class SemverSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        settings.gradle.beforeProject {
            it.gradle.startParameter
            it.version = "0.1.0"
        }
    }
}
