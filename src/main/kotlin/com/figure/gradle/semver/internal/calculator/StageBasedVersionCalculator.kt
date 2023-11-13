package com.figure.gradle.semver.internal.calculator

import com.figure.gradle.semver.internal.Modifier
import com.figure.gradle.semver.internal.Stage
import com.figure.gradle.semver.internal.command.KGit
import com.figure.gradle.semver.internal.extensions.newPreRelease
// import com.figure.gradle.semver.internal.extensions.nextPreRelease
import com.figure.gradle.semver.internal.extensions.nextSnapshot
import com.figure.gradle.semver.internal.extensions.nextStable
// import com.figure.gradle.semver.internal.extensions.currentStage
import com.figure.gradle.semver.internal.extensions.toInc
import com.figure.gradle.semver.internal.modifierProperty
import com.figure.gradle.semver.internal.stageProperty
import io.github.z4kn4fein.semver.Version
import org.gradle.api.Project

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
internal class StageBasedVersionCalculator(
    private val kGit: KGit,
) : VersionCalculator {
    override fun calculate(rootProject: Project, latestVersion: Version): String {
        val stage = rootProject.stageProperty.get()
        val modifier = rootProject.modifierProperty.get()
        val incrementer = modifier.value.uppercase().toInc()

        val nextVersion = when {
            // next stable
            shouldComputeNextStable(stage, latestVersion) -> {
                latestVersion.nextStable(incrementer)
            }
            // next snapshot (next stable with SNAPSHOT label)
            shouldComputeNextSnapshot(stage) -> {
                latestVersion.nextSnapshot(incrementer)
            }
            // new pre-release (last version is stable or different pre-release stage, next is new pre-release)
            shouldComputeNewPreRelease(stage, modifier, latestVersion) -> {
                latestVersion.newPreRelease(incrementer, stage)
            }
            // next pre-release (last version is pre-release, next is pre-release)
            shouldComputeNextPreRelease(stage, modifier, latestVersion) -> {
                TODO()
                // latestVersion.nextPreRelease()
            }

            else -> {
                error(
                    "Could not determine next version for: " +
                        "stage=${stage.value} modifier=${modifier.value} latest version=$latestVersion"
                )
            }
        }

        return nextVersion.toString()
    }

    private fun shouldComputeNextStable(stage: Stage, latestVersion: Version): Boolean =
        stage == Stage.Auto && latestVersion.isStable

    /**
     * Should compute next stable with SNAPSHOT label?
     */
    private fun shouldComputeNextSnapshot(stage: Stage): Boolean =
        stage == Stage.Snapshot

    /**
     * Stage is provided (and it's not AUTO) AND either:
     * - the latest version is stable
     * - the latest version is pre-release and the modifier is auto and the latest pre-release label isn't the
     *   same as the provided stage
     */
    private fun shouldComputeNewPreRelease(stage: Stage, modifier: Modifier, latestVersion: Version): Boolean =
        stage != Stage.Auto && (
            latestVersion.isStable || (
                latestVersion.isPreRelease &&
                    modifier == Modifier.Auto &&
                    latestVersion.preRelease?.lowercase() != stage.value.lowercase()
                )
            )

    private fun shouldComputeNextPreRelease(
        stage: Stage,
        modifier: Modifier,
        latestVersion: Version,
    ): Boolean {
        TODO()
        // if (latestVersion.isPreRelease && latestVersion.currentStage() == stage) {
        //     return true
        // }
        //
        //
        // return stage != Stage.Auto && latestVersion.isPreRelease
    }
}
