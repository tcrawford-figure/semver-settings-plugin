package io.github.tcrawford.gradle.semver.internal.calculator

import io.github.tcrawford.gradle.semver.internal.Modifier
import io.github.tcrawford.gradle.semver.internal.Stage
import io.github.z4kn4fein.semver.Version

internal interface VersionCalculator {
    fun calculate(latestVersion: Version, stage: Stage, modifier: Modifier, forTesting: Boolean): String
}
