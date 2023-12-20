package io.github.tcrawford.versioning.internal.calculator

import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.nextPreRelease

object GitStateVersionCalculator : VersionCalculator {
    override fun calculate(latestVersion: Version, context: VersionCalculatorContext): String = with(context) {
        return latestVersion.nextPreRelease(gitState.description).toString()
    }
}
