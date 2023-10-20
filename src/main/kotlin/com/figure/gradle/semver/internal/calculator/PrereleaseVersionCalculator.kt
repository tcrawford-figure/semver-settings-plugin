package com.figure.gradle.semver.internal.calculator

/**
 * Should calculate the next version based on the prerelease identifier provided.
 *
 * TODO: Support the following:
 *   - alpha
 *   - beta
 *   - rc
 *   - snapshot
 *   - ga
 *   - final
 *
 * Use Gradle's version ordering (if kotlin-semver doesn't support it) to determine prerelease ordering:
 * https://docs.gradle.org/current/userguide/single_versions.html#version_ordering
 */
class PrereleaseVersionCalculator : VersionCalculator {
    override fun calculate(): String {
        TODO("Not yet implemented")
    }
}
