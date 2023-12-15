package io.github.tcrawford.versioning.internal.calculator

import io.github.tcrawford.versioning.internal.command.GitState
import io.github.tcrawford.versioning.internal.properties.Modifier
import io.github.tcrawford.versioning.internal.properties.Stage

data class VersionCalculatorContext(
    val stage: Stage,
    val modifier: Modifier,
    val forTesting: Boolean,
    val gitState: GitState,
    val mainBranch: String? = null,
    val developmentBranch: String? = null,
)
