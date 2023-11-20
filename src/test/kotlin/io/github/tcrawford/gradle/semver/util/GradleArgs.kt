/**
 * Copyright (c) 2023 Figure Technologies and its affiliates.
 *
 * This source code is licensed under the Apache 2.0 license found in the
 * LICENSE.md file in the root directory of this source tree.
 */

package io.github.tcrawford.gradle.semver.util

import io.github.tcrawford.gradle.semver.internal.Modifier
import io.github.tcrawford.gradle.semver.internal.SemverProperty
import io.github.tcrawford.gradle.semver.internal.Stage

/**
 * Note that the parameters must be provided in the format: `-P<property>=<value>` and not `-P <property>=<value>`.
 */
internal object GradleArgs {
    const val Parallel = "--parallel"
    const val BuildCache = "--build-cache"
    const val ConfigurationCache = "--configuration-cache"
    const val Stacktrace = "--stacktrace"

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
}
