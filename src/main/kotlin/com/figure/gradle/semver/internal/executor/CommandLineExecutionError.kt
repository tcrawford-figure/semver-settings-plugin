package com.figure.gradle.semver.internal.executor

class CommandLineExecutionError(command: String) : Exception(
    "An error occurred executing command: $command"
)
