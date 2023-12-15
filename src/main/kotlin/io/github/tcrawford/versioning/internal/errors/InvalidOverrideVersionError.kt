package io.github.tcrawford.versioning.internal.errors

class InvalidOverrideVersionError(
    invalidVersion: String,
) : Exception(
        "Invalid override version provided: $invalidVersion",
    )
