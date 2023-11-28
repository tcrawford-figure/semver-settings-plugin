package io.github.tcrawford.gradle.semver.internal.extensions

import org.gradle.api.flow.FlowScope
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.support.serviceOf

internal val Settings.flowScope: FlowScope
    get() = serviceOf<FlowScope>()
