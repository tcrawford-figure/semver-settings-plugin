package com.figure.gradle.semver.internal.git

import com.figure.gradle.semver.internal.executor.CommandLineExecutor
import com.github.michaelbull.result.getOrThrow

internal class TagCommand(
    private val execute: CommandLineExecutor
) {
    operator fun invoke(tagName: String): String =
        execute("git tag $tagName").getOrThrow()

    fun delete(tagName: String): String =
        execute("git tag -d $tagName").getOrThrow()
}
