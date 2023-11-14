package io.github.tcrawford.gradle.semver.util

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

fun BuildResult.taskOutcome(taskName: String): TaskOutcome? {
    return checkNotNull(task(":$taskName")).outcome
}
