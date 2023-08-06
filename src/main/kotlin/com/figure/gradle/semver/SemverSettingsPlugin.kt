package com.figure.gradle.semver

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class SemverSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        val git = Git.open(settings.settingsDir)

        val tagPatternRegex = "^v\\d+\\.\\d+\\.\\d+$".toRegex()

        val repository = git.repository

        val tags = git.tagList().call().map { it.name.replace("refs/tags/", "") }
        println("tags: $tags")

        val emptyString = ""

        val mostRecentTag = git.tagList()
            .call()
            .map { it.name.replace(Constants.R_TAGS, emptyString) }
            .filter { it.matches(tagPatternRegex) }
            .maxByOrNull { it } // Assumes tags are in the form 'vX.X.X' and are comparable as strings

        println("Most recent tag: $mostRecentTag")

        val commitsSinceLastTag = git.log()
            .addRange(repository.resolve(mostRecentTag), repository.resolve(Constants.HEAD))
            .call()
            .toList()
            .size

        println("Commits since last tag: $commitsSinceLastTag")

        settings.gradle.beforeProject {
            it.version = "0.1.0"
        }
    }
}
