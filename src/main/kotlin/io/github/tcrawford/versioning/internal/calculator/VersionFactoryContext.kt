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
package io.github.tcrawford.versioning.internal.calculator

import io.github.tcrawford.versioning.internal.properties.BuildMetadataOptions
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
    val forMajorVersion: Int?,
    val rootDir: File,
    val mainBranch: String?,
    val developmentBranch: String?,
    val appendBuildMetadata: BuildMetadataOptions,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}
