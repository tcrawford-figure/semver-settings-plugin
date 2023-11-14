package com.figure.gradle.semver.internal.calculator

import com.figure.gradle.semver.internal.Modifier
import com.figure.gradle.semver.internal.Stage
import com.figure.gradle.semver.internal.extensions.nextVersion
import io.github.z4kn4fein.semver.Version

/**
 * Calculates the next version based on the stage and modifier.
 */
internal class StageBasedVersionCalculator : VersionCalculator {
    override fun calculate(latestVersion: Version, stage: Stage, modifier: Modifier, forTesting: Boolean): String {
        return latestVersion.nextVersion(stage, modifier).toString()
    }
}
