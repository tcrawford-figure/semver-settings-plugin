package com.figure.gradle.semver.internal.calculator

import com.figure.gradle.semver.SemverExtension
import com.figure.gradle.semver.internal.command.KGit
import com.figure.gradle.semver.internal.extensions.toInc
import com.figure.gradle.semver.internal.modifierProperty
import io.github.z4kn4fein.semver.inc
import io.github.z4kn4fein.semver.toVersion
import org.gradle.api.Project

/**
 * Should calculate the next stable semantic version
 */
class StableVersionCalculator : VersionCalculator {
    override fun calculate(semverExtension: SemverExtension, rootProject: Project): String {
        val kgit = KGit(directory = rootProject.rootDir)

        val incrementer = rootProject.modifierProperty.map { it.uppercase().toInc() }.get()

        val latestTag = kgit.tags.latest ?: semverExtension.initialVersion.map { it.toVersion() }.get()
        return latestTag.inc(incrementer).toString()
    }
}
