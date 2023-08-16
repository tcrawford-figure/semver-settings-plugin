package com.figure.gradle.semver

import com.figure.gradle.semver.internal.git.Git
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

internal val log: Logger = Logging.getLogger(Logger.ROOT_LOGGER_NAME)

class SemverSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        val git = Git(settings)

        val gitVersion = git.version()
        log.lifecycle("Found git version: $gitVersion")

        // This is cheeky. Continue to try this for more fun!
        git.push.tag("v1.0.0")

        settings.gradle.beforeProject {
            it.version = "0.1.0"
        }
    }
}
