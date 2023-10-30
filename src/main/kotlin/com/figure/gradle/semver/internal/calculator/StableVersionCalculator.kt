package com.figure.gradle.semver.internal.calculator

import com.figure.gradle.semver.internal.extensions.toInc
import com.figure.gradle.semver.internal.modifierProperty
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.inc
import org.gradle.api.Project

/**
 * Should calculate the next stable semantic version
 */
internal class StableVersionCalculator : VersionCalculator {
    override fun calculate(rootProject: Project, latestVersion: Version): String {
        val incrementer = rootProject.modifierProperty.map { it.value.uppercase().toInc() }.get()
        return latestVersion.inc(incrementer).toString()
    }
}
