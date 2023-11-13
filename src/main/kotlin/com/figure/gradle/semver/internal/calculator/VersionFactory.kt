package com.figure.gradle.semver.internal.calculator

import com.figure.gradle.semver.SemverExtension
import com.figure.gradle.semver.internal.Stage
import com.figure.gradle.semver.internal.command.KGit
import com.figure.gradle.semver.internal.forTesting
import com.figure.gradle.semver.internal.modifierProperty
import com.figure.gradle.semver.internal.stageProperty
import com.figure.gradle.semver.internal.tagPrefixProperty
import com.figure.gradle.semver.log
import org.gradle.api.Project

internal object VersionFactory {
    fun nextVersion(semverExtension: SemverExtension, rootProject: Project): String {
        val kgit = KGit(directory = rootProject.rootDir)
        val forTesting = rootProject.forTesting.get()

        val stage = rootProject.stageProperty.get()
        val modifier = rootProject.modifierProperty.get()
        val tagPrefix = rootProject.tagPrefixProperty.get()

        val currentBranch = kgit.branch.currentRef(forTesting).name

        log.lifecycle("Stage: $stage")
        log.lifecycle("Modifier: $modifier")
        log.lifecycle("Tag prefix: $tagPrefix")
        log.lifecycle("Current Branch: $currentBranch")
        log.lifecycle("Main branch: ${kgit.branches.mainBranch.name}")
        log.lifecycle("Development branch: ${kgit.branches.developmentBranch.name}")

        val latestVersion = kgit.tags.latestOrInitial(semverExtension.initialVersion)

        val version = when {
            // TODO: Handle version override as highest priority

            kgit.branch.isOnMainBranch(forTesting) -> {
                val stageBasedVersionCalculator = StageBasedVersionCalculator()
                stageBasedVersionCalculator.calculate(rootProject, latestVersion)
            }
            // Works for any branch
            else -> {
                // Compute based on the branch name, otherwise, use the stage to compute the next version
                if (stage == Stage.Auto) {
                    val branchBasedVersionCalculator = BranchBasedVersionCalculator(kgit)
                    branchBasedVersionCalculator.calculate(rootProject, latestVersion)
                } else {
                    val stageBasedVersionCalculator = StageBasedVersionCalculator()
                    stageBasedVersionCalculator.calculate(rootProject, latestVersion)
                }
            }
        }

        return version
    }
}
