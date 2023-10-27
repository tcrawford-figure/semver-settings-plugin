package com.figure.gradle.semver.internal.calculator

import com.figure.gradle.semver.SemverExtension
import com.figure.gradle.semver.internal.command.KGit
import org.gradle.api.Project

/**
 * Should calculate the next version based on the current branch name
 */
class BranchBasedVersionCalculator : VersionCalculator {
    override fun calculate(semverExtension: SemverExtension, rootProject: Project): String {
        val kgit = KGit(directory = rootProject.rootDir)

        // val latestTag = kgit.tags.latestOrInitial(semverExtension.initialVersion)

        // TODO: Test checking out the head ref and figure out the branch it belongs to from there
        kgit.checkout(kgit.branch.headCommit.name)

        return "0.0.1"
    }
}
