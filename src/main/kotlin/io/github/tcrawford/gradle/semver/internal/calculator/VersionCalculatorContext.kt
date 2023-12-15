package io.github.tcrawford.gradle.semver.internal.calculator

import io.github.tcrawford.gradle.semver.internal.command.GitState
import io.github.tcrawford.gradle.semver.internal.properties.Modifier
import io.github.tcrawford.gradle.semver.internal.properties.Stage

data class VersionCalculatorContext(
    val stage: Stage,
    val modifier: Modifier,
    val forTesting: Boolean,
    val gitState: GitState,
    val mainBranch: String? = null,
    val developmentBranch: String? = null,
)
