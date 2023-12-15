package io.github.tcrawford.versioning.internal.extensions

import org.gradle.api.Project
import org.gradle.api.flow.FlowScope
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginAware
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.support.serviceOf
import java.io.File

val PluginAware.providers: ProviderFactory
    get() = when (this) {
        is Settings -> providers
        is Project -> providers
        else -> error("Not a project or settings")
    }

val PluginAware.rootDir: File
    get() = when (this) {
        is Settings -> settingsDir
        is Project -> rootDir
        else -> error("Not a project or settings")
    }

val PluginAware.projectDir: File
    get() = when (this) {
        is Settings -> settingsDir
        is Project -> projectDir
        else -> error("Not a project or settings")
    }

val PluginAware.gradle: Gradle
    get() = when (this) {
        is Settings -> gradle
        is Project -> gradle
        else -> error("Not a project or settings")
    }

val PluginAware.extensions: ExtensionContainer
    get() = when (this) {
        is Settings -> extensions
        is Project -> extensions
        else -> error("Not a project or settings")
    }

val PluginAware.flowScope: FlowScope
    get() = when (this) {
        is Project -> serviceOf<FlowScope>()
        is Settings -> serviceOf<FlowScope>()
        else -> error("Not a project or settings")
    }
