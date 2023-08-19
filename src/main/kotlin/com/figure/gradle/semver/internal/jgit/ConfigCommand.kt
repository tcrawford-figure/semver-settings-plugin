package com.figure.gradle.semver.internal.jgit

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ConfigConstants

class ConfigCommand(
    private val git: Git
) {
    fun author(name: String, email: String) {
        val config = git.repository.config

        config.setString(
            ConfigConstants.CONFIG_USER_SECTION,
            null,
            ConfigConstants.CONFIG_KEY_NAME,
            name
        )
        config.setString(
            ConfigConstants.CONFIG_USER_SECTION,
            null,
            ConfigConstants.CONFIG_KEY_EMAIL,
            email
        )

        config.save()
    }
}
