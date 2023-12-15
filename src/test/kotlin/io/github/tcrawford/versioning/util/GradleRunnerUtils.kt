package io.github.tcrawford.versioning.util

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

fun GradleRunner.runTask(arguments: List<String>): Pair<BuildResult, BuildResult> {
    val firstRun = this
        .withArguments(arguments)
        .build()

    val secondRun = this
        .withArguments(arguments)
        .build()

    return firstRun to secondRun
}
