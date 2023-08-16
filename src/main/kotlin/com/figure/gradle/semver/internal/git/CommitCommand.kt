package com.figure.gradle.semver.internal.git

import com.figure.gradle.semver.internal.executor.CommandLineExecutor
import com.github.michaelbull.result.getOrThrow

internal class CommitCommand(
    private val execute: CommandLineExecutor
) {
    operator fun invoke(message: String, allowEmptyCommit: Boolean = false): String =
        execute(
            buildString {
                append("git commit")
                if (allowEmptyCommit) {
                    append(" --allow-empty")
                }
                append("-m \"$message\"")
            }
        ).getOrThrow()
}
