package io.github.tcrawford.gradle.semver.utils

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.util.GradleVersion
import java.io.File

fun build(
    gradleVersion: GradleVersion,
    projectDir: File,
    vararg args: String
): BuildResult =
    runner(gradleVersion, projectDir, *args).build()

fun buildAndFail(
    gradleVersion: GradleVersion,
    projectDir: File,
    vararg args: String
): BuildResult =
    runner(gradleVersion, projectDir, *args).buildAndFail()

fun runWithoutExpectations(
    gradleVersion: GradleVersion,
    projectDir: File,
    vararg args: String
): BuildResult =
    runner(gradleVersion, projectDir, *args).run()

fun runner(
    gradleVersion: GradleVersion,
    projectDir: File,
    vararg args: String,
): GradleRunner = GradleRunner.create().apply {
    val arguments = setOf(
        *args,
        "--parallel",
        "--build-cache",
        "--configuration-cache",
        "--stacktrace",
    ).toList()

    forwardOutput()
    withGradleVersion(gradleVersion.version)
    withProjectDir(projectDir)
    withArguments(arguments)

}
