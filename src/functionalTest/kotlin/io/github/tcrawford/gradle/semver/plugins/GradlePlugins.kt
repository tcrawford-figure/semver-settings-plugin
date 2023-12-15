package io.github.tcrawford.gradle.semver.plugins

import com.autonomousapps.kit.AbstractGradleProject.Companion.PLUGIN_UNDER_TEST_VERSION
import com.autonomousapps.kit.gradle.Plugin

object GradlePlugins {
    val KOTLIN_VERSION: String = KotlinVersion.CURRENT.toString()
    val gitAwareVersioningPluginId = "io.github.tcrawford.gradle.semver"

    val gitAwareVersioningPlugin: Plugin = Plugin(gitAwareVersioningPluginId, PLUGIN_UNDER_TEST_VERSION)

    val kotlinNoVersion: Plugin = Plugin("org.jetbrains.kotlin.jvm", null, true)
    val kotlinNoApply: Plugin = Plugin("org.jetbrains.kotlin.jvm", KOTLIN_VERSION, false)
}
