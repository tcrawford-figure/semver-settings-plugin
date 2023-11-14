package com.figure.gradle.semver.internal.command

import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.toVersion
import io.github.z4kn4fein.semver.toVersionOrNull
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref
import org.gradle.api.provider.Property

internal class TagList(
    private val git: Git
) {
    operator fun invoke(): MutableList<Ref>? =
        git.tagList().call()

    fun find(tagName: String): Ref? =
        git.tagList().call().find { it.name == tagName }

    val latest: Version?
        get() = git.tagList().call()
            .mapNotNull { it.name.replace("refs/tags/", "").toVersionOrNull(strict = false) }
            .maxOrNull()

    fun latestOrInitial(initial: Property<String>): Version =
        latest ?: initial.get().toVersion()
}
