package io.github.tcrawford.gradle.semver.internal.errors

class InvalidOverrideVersionError(
    invalidVersion: String
) : Exception(
    "Invalid override version provided: $invalidVersion"
)
