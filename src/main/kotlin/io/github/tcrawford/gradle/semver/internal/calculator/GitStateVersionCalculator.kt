package io.github.tcrawford.gradle.semver.internal.calculator

import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.nextPreRelease

class GitStateVersionCalculator : VersionCalculator {
    override fun calculate(latestVersion: Version, context: VersionCalculatorContext): String = with(context) {
        return latestVersion.nextPreRelease(gitState.description).toString()
    }
}
