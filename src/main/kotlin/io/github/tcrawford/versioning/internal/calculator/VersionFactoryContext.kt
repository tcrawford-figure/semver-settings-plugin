package io.github.tcrawford.versioning.internal.calculator

import io.github.tcrawford.versioning.internal.properties.Modifier
import io.github.tcrawford.versioning.internal.properties.Stage
import java.io.File
import java.io.Serializable

data class VersionFactoryContext(
    val initialVersion: String,
    val stage: Stage,
    val modifier: Modifier,
    val forTesting: Boolean,
    val overrideVersion: String?,
    val rootDir: File,
    val mainBranch: String?,
    val developmentBranch: String?,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
