package com.figure.gradle.semver.internal.calculator

import com.figure.gradle.semver.internal.SemverContext
import org.gradle.api.Project

/**
 * Should calculate the next version based on the current branch name
 */
class BranchBasedVersionCalculator : VersionCalculator {
    override fun calculate(semverContext: SemverContext, rootProject: Project): String {
        TODO("Not yet implemented")
    }
}
