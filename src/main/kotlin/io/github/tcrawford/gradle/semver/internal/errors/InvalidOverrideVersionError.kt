package io.github.tcrawford.gradle.semver.internal.errors

internal class InvalidOverrideVersionError(
    invalidVersion: String
) : Exception(
    "Invalid override version provided: $invalidVersion"
)
