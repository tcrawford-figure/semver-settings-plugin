package io.github.tcrawford.versioning.internal.calculator

import io.github.tcrawford.versioning.internal.extensions.nextVersion
import io.github.z4kn4fein.semver.Version

/**
 * Calculates the next version based on the stage and modifier.
 */
class StageVersionCalculator : VersionCalculator {
    override fun calculate(latestVersion: Version, context: VersionCalculatorContext): String = with(context) {
        return latestVersion.nextVersion(stage, modifier).toString()
    }
}
