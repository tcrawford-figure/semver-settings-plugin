package io.github.tcrawford.versioning.util

import io.github.tcrawford.versioning.internal.properties.Modifier
import io.github.tcrawford.versioning.internal.properties.SemverProperty
import io.github.tcrawford.versioning.internal.properties.Stage

/**
 * The parameters must be provided in the format: `-P<property>=<value>` and not `-P <property>=<value>`.
 */
internal object GradleArgs {
    fun semverStage(stage: Stage) =
        "-P${SemverProperty.Stage.property}=${stage.value}"

    fun semverModifier(modifier: Modifier) =
        "-P${SemverProperty.Modifier.property}=${modifier.value}"

    fun semverTagPrefix(tagPrefix: String) =
        "-P${SemverProperty.TagPrefix.property}=$tagPrefix"

    fun semverForTesting(forTesting: Boolean) =
        "-P${SemverProperty.ForTesting.property}=$forTesting"

    fun semverOverrideVersion(overrideVersion: String) =
        "-P${SemverProperty.OverrideVersion.property}=$overrideVersion"

    fun semverForMajorVersion(forMajorVersion: Int) =
        "-P${SemverProperty.ForMajorVersion.property}=$forMajorVersion"
}
