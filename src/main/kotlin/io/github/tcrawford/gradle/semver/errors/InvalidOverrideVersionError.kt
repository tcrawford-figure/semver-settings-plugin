package io.github.tcrawford.gradle.semver.errors

internal class InvalidOverrideVersionError(
    invalidVersion: String
) : Exception(
    "Invalid override version provided: $invalidVersion"
)
