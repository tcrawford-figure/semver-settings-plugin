package io.github.tcrawford.gradle.semver.internal.calculator

import io.github.tcrawford.gradle.semver.internal.Modifier
import io.github.tcrawford.gradle.semver.internal.Stage
import io.github.tcrawford.gradle.semver.internal.extensions.nextVersion
import io.github.z4kn4fein.semver.Version

/**
 * Calculates the next version based on the stage and modifier.
 */
internal class StageBasedVersionCalculator : VersionCalculator {
    override fun calculate(latestVersion: Version, stage: Stage, modifier: Modifier, forTesting: Boolean): String {
        return latestVersion.nextVersion(stage, modifier).toString()
    }
}
