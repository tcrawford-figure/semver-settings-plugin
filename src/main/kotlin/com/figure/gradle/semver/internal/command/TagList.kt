package com.figure.gradle.semver.internal.command

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref

internal class TagList(
    private val git: Git,
) {
    operator fun invoke(): MutableList<Ref>? =
        git.tagList().call()

    fun find(tagName: String): Ref? =
        git.tagList().call().find { it.name == tagName }
}
