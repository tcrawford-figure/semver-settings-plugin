package io.github.tcrawford.gradle.semver.internal.calculator

import io.github.z4kn4fein.semver.Version

interface VersionCalculator {
    fun calculate(latestVersion: Version, context: VersionCalculatorContext): String
}
