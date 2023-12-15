package io.github.tcrawford.gradle.semver

import io.github.tcrawford.gradle.semver.internal.extensions.extensions
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.initialization.Settings
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.PluginAware
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

/**
 * Configuration for the Semver Settings Plugin that enables:
 * - Manually setting the rootProjectDir
 * - An initial version
 * - The main branch, if not `main` or `master`
 * - The development branch if not `develop`, `devel`, or `dev`
 *
 */
open class SemverExtension @Inject constructor(
    objects: ObjectFactory,
) {
    companion object {
        const val NAME = "semver"

        operator fun invoke(pluginAware: PluginAware) =
            pluginAware.extensions.create<SemverExtension>(NAME)
    }

    val rootProjectDir: RegularFileProperty = objects.fileProperty()
    val initialVersion: Property<String> = objects.property<String>().convention("0.0.0")
    val mainBranch: Property<String> = objects.property()
    val developmentBranch: Property<String> = objects.property()
}

fun Settings.semver(configure: SemverExtension.() -> Unit): Unit =
    (this as ExtensionAware).extensions.configure(SemverExtension.NAME, configure)
