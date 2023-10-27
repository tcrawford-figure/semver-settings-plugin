package com.figure.gradle.semver.internal.calculator

import com.figure.gradle.semver.internal.SemverContext
import org.gradle.api.Project

interface VersionCalculator {
    fun calculate(semverContext: SemverContext, rootProject: Project): String
}
