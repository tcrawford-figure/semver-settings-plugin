package io.github.tcrawford.gradle.semver.internal.calculator

import io.github.tcrawford.gradle.semver.internal.Modifier
import io.github.tcrawford.gradle.semver.internal.Stage
import java.io.File
import java.io.Serializable

internal data class VersionFactoryContext(
    val initialVersion: String,
    val stage: Stage,
    val modifier: Modifier,
    val forTesting: Boolean,
    val overrideVersion: String?,
    val rootDir: File,
    val mainBranch: String?,
    val developmentBranch: String?
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
