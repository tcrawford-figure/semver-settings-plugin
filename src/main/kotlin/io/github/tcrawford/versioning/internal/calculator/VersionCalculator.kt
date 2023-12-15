package io.github.tcrawford.versioning.internal.calculator

import io.github.z4kn4fein.semver.Version

interface VersionCalculator {
    fun calculate(latestVersion: Version, context: VersionCalculatorContext): String
}
