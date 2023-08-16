@file:Suppress("UnstableApiUsage")

package com.figure.gradle.semver.internal.executor

import com.figure.gradle.semver.log
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.codehaus.plexus.util.cli.CommandLineUtils
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.LogLevel
import java.io.File

class CommandLineExecutor(
    private val settings: Settings,
    private val logLevel: LogLevel = LogLevel.INFO,
) {
    operator fun invoke(
        command: String,
        workingDirectory: File = settings.settingsDir,
        logStdout: Boolean = false,
        logStderr: Boolean = false,
    ): Result<String, CommandLineExecutionError> {
        val splitCommand = CommandLineUtils.translateCommandline(command)

        val result = settings.providers.exec {
            it.workingDir = workingDirectory
            it.commandLine(splitCommand)
            it.isIgnoreExitValue = true
        }

        val stderr = result.standardError.asText.get()
        if (logStderr && stderr.isNotEmpty()) {
            log.error(stderr)
        }

        val stdout = result.standardOutput.asText.get()
        if (logStdout && stdout.isNotEmpty()) {
            log.log(logLevel, stdout)
        }

        if (result.result.get().exitValue > 0) {
            Err(CommandLineExecutionError(command))
        }

        return Ok(stdout)
    }
}
