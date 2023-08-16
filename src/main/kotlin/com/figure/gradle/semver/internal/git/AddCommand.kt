package com.figure.gradle.semver.internal.git

import com.figure.gradle.semver.internal.executor.CommandLineExecutor
import com.github.michaelbull.result.getOrThrow

internal class AddCommand(
    private val execute: CommandLineExecutor
) {
    operator fun invoke(filename: String): String =
        execute("git add $filename").getOrThrow()

    fun all(): String =
        execute("git add --all").getOrThrow()
}
