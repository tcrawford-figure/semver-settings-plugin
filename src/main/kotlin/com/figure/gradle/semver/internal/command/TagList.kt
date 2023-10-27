package com.figure.gradle.semver.internal.command

import com.figure.gradle.semver.log
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.toVersionOrNull
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref

internal class TagList(
    private val git: Git,
) {
    operator fun invoke(): MutableList<Ref>? =
        git.tagList().call()

    fun find(tagName: String): Ref? =
        git.tagList().call().find { it.name == tagName }

    val latest: Version?
        get() {
            // TODO: Figure out why this tagList call isn't working, but the one during test setup does work
            git.tagList().call().forEach {
                log.lifecycle("Found tag: ${it.name}")
            }

            return git.tagList().call()
                .mapNotNull { it.name.replace("refs/tags/", "").toVersionOrNull(strict = false) }
                .maxOrNull()
        }
}
