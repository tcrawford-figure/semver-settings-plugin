package io.github.tcrawford.gradle.semver.internal.logging

import io.github.tcrawford.gradle.semver.internal.extensions.flowScope
import org.gradle.api.flow.FlowAction
import org.gradle.api.flow.FlowParameters
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.always

private val log = Logging.getLogger(Logger.ROOT_LOGGER_NAME)

internal fun Settings.registerPostBuildVersionLogMessage(message: String) {
    flowScope.always(PostBuildVersionLogger::class) { action ->
        action.parameters.message.set(message)
    }
}

private abstract class PostBuildVersionLogger : FlowAction<PostBuildVersionLogger.Params> {
    interface Params : FlowParameters {
        @get:Input
        val message: Property<String>
    }

    override fun execute(parameters: Params) {
        log.lifecycle { parameters.message.get() }
    }
}
