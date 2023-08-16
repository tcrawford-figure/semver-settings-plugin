package com.figure.gradle.semver.internal.git

import com.figure.gradle.semver.internal.executor.CommandLineExecutor
import com.github.michaelbull.result.getOrThrow
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.LogLevel

/**
 * TODO:
 *   - Make "origin" configurable. Assuming origin for now is fine.
 */
internal class Git(
    settings: Settings,
) {
    private val execute = CommandLineExecutor(settings, LogLevel.LIFECYCLE)

    val add: AddCommand = AddCommand(execute)
    val commit: CommitCommand = CommitCommand(execute)
    val push: PushCommand = PushCommand(execute)
    val tag: TagCommand = TagCommand(execute)

    fun version(): String =
        execute("git --version").getOrThrow()
}
