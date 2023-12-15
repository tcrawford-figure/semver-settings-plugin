package io.github.tcrawford.versioning.plugins

import com.autonomousapps.kit.AbstractGradleProject.Companion.PLUGIN_UNDER_TEST_VERSION
import com.autonomousapps.kit.gradle.Plugin

object GradlePlugins {
    val KOTLIN_VERSION: String = KotlinVersion.CURRENT.toString()
    val gitAwareVersioningPluginId = "io.github.tcrawford.versioning"

    val gitAwareVersioningPlugin: Plugin = Plugin(gitAwareVersioningPluginId, PLUGIN_UNDER_TEST_VERSION)

    val kotlinNoVersion: Plugin = Plugin("org.jetbrains.kotlin.jvm", null, true)
    val kotlinNoApply: Plugin = Plugin("org.jetbrains.kotlin.jvm", KOTLIN_VERSION, false)
}
