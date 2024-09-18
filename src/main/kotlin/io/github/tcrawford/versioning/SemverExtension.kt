package io.github.tcrawford.versioning

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property

/**
 * Configuration for the Semver Settings Plugin that enables:
 * - Manually setting the rootProjectDir
 * - An initial version
 * - The main branch, if not `main` or `master`
 * - The development branch if not `develop`, `devel`, or `dev`
 *
 */
interface SemverExtension {
    val rootProjectDir: RegularFileProperty
    val initialVersion: Property<String>
    val mainBranch: Property<String>
    val developmentBranch: Property<String>

    companion object {
        const val NAME = "semver"
    }
}

fun Settings.semver(configure: SemverExtension.() -> Unit): Unit =
    (this as ExtensionAware).extensions.configure(SemverExtension.NAME, configure)
