package com.figure.gradle.semver.internal.calculator

// import com.figure.gradle.semver.internal.extensions.nextPreRelease
import com.figure.gradle.semver.internal.extensions.nextVersion
// import com.figure.gradle.semver.internal.extensions.currentStage
import com.figure.gradle.semver.internal.modifierProperty
import com.figure.gradle.semver.internal.stageProperty
import io.github.z4kn4fein.semver.Version
import org.gradle.api.Project

/**
 * Calculates the next version based on the stage and modifier.
 */
internal class StageBasedVersionCalculator : VersionCalculator {
    override fun calculate(rootProject: Project, latestVersion: Version): String {
        val stage = rootProject.stageProperty.get()
        val modifier = rootProject.modifierProperty.get()
        return latestVersion.nextVersion(stage, modifier).toString()
    }
}
