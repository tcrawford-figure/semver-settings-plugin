package com.figure.gradle.semver.internal.jgit

import com.github.michaelbull.result.getOrThrow
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref

class TagCommandWrapper(
    private val git: Git
) {
    operator fun invoke(tagName: String): Ref? =
        git.tag()
            .setName(tagName)
            .call()

    fun delete(vararg tag: String): MutableList<String>? =
        git.tagDelete()
            .setTags(*tag)
            .call()
}
