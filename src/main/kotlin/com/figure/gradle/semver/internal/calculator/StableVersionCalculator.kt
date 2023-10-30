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
internal class StableVersionCalculator(
    private val kGit: KGit,
) : VersionCalculator {
    override fun calculate(semverExtension: SemverExtension, rootProject: Project): String {
        val incrementer = rootProject.modifierProperty.map { it.value.uppercase().toInc() }.get()

        val latestTag = kGit.tags.latest ?: semverExtension.initialVersion.map { it.toVersion() }.get()
        return latestTag.inc(incrementer).toString()
    }
}
