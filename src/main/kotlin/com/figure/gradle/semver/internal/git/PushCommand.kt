package com.figure.gradle.semver.internal.git

import com.figure.gradle.semver.internal.executor.CommandLineExecutor
import com.github.michaelbull.result.getOrThrow

internal class PushCommand(
    private val execute: CommandLineExecutor
) {
    operator fun invoke(): String =
        execute("git push").getOrThrow()

    fun tag(tagName: String): String =
        execute("git push origin $tagName").getOrThrow()

    fun branch(branch: String): String =
        execute("git push -u origin $branch").getOrThrow()

    fun allTags(): String =
        execute("git push origin --tags").getOrThrow()
}
