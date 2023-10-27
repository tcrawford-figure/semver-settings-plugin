package com.figure.gradle.semver.internal.calculator

import com.figure.gradle.semver.internal.SemverContext
import com.figure.gradle.semver.internal.command.KGit
import com.figure.gradle.semver.internal.extensions.toInc
import com.figure.gradle.semver.internal.modifierProperty
import io.github.z4kn4fein.semver.Inc
import io.github.z4kn4fein.semver.inc
import org.gradle.api.Project

/**
 * Should calculate the next stable semantic version
 */
class StableVersionCalculator : VersionCalculator {
    override fun calculate(semverContext: SemverContext, rootProject: Project): String {
        val kgit = KGit(directory = rootProject.rootDir)

        val modifier = rootProject.modifierProperty.map { it.uppercase().toInc() }.getOrElse(Inc.PATCH)

        return kgit.tags.latest?.inc(modifier)?.toString()
        // TODO: Make this an extension value for what the initial version is. Might want to use the initial version
        //  if the latest can't be found, and then increment it?
            ?: "0.0.1"
    }
}
