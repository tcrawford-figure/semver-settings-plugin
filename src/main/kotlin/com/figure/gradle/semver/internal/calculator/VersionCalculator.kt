package com.figure.gradle.semver.internal.calculator

import com.figure.gradle.semver.SemverExtension
import org.gradle.api.Project

interface VersionCalculator {
    fun calculate(semverExtension: SemverExtension, rootProject: Project): String
}
