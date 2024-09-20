/*
 * Copyright (C) 2024 Tyler Crawford
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    fun semverAppendBuildMetadata(buildMetadataOptions: String) =
        "-P${SemverProperty.AppendBuildMetadata.property}=$buildMetadataOptions"
}
