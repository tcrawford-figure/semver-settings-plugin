package com.figure.gradle.semver.internal.calculator

import io.github.z4kn4fein.semver.Version
import org.gradle.api.Project

internal interface VersionCalculator {
    fun calculate(rootProject: Project, latestVersion: Version): String
}
