package com.figure.gradle.semver.internal.calculator

import com.figure.gradle.semver.SemverExtension
import com.figure.gradle.semver.internal.command.KGit
import com.figure.gradle.semver.internal.extensions.sanitizedWithoutPrefix
import com.figure.gradle.semver.internal.extensions.toInc
import com.figure.gradle.semver.internal.forTesting
import com.figure.gradle.semver.internal.modifierProperty
import io.github.z4kn4fein.semver.inc
import org.gradle.api.Project

/**
 * Should calculate the next version based on the current branch name
 */
internal class BranchBasedVersionCalculator(
    private val kGit: KGit,
) : VersionCalculator {
    override fun calculate(semverExtension: SemverExtension, rootProject: Project): String {
        val latestVersion = kGit.tags.latestOrInitial(semverExtension.initialVersion)
        val currentBranch = kGit.branch.currentRef(rootProject.forTesting.get())
        val developmentBranch = kGit.branches.developmentBranch

        val prereleaseLabel = currentBranch.sanitizedWithoutPrefix()
        val commitCount = kGit.branches.commitCountBetween(developmentBranch.name, currentBranch.name)
        val prereleaseLabelWithCommitCount = "$prereleaseLabel.$commitCount"

        val incrementer = rootProject.modifierProperty.map { it.value.uppercase().toInc() }.get()

        return latestVersion.inc(incrementer).copy(preRelease = prereleaseLabelWithCommitCount).toString()
    }
}
