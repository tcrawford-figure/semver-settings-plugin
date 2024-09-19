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
package io.github.tcrawford.versioning.specs

import io.github.tcrawford.versioning.gradle.semver
import io.github.tcrawford.versioning.internal.properties.BuildMetadataOptions
import io.github.tcrawford.versioning.kotest.GradleProjectsExtension
import io.github.tcrawford.versioning.kotest.shouldOnlyHave
import io.github.tcrawford.versioning.kotest.shouldOnlyMatch
import io.github.tcrawford.versioning.projects.RegularProject
import io.github.tcrawford.versioning.projects.SettingsProject
import io.github.tcrawford.versioning.projects.SubprojectProject
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import org.gradle.util.GradleVersion

class CalculateNextVersionWithBuildMetadataSpec : FunSpec({
    val mainBranch = "main"
    val featureBranch = "feature/cool/branch"
    val developmentBranch = "develop"

    context("should not append build metadata") {
        val semver = semver {
            appendBuildMetadata = "invalid"
        }

        val projects = install(
            GradleProjectsExtension(
                RegularProject(projectName = "regular-project", semver = semver),
                SettingsProject(projectName = "settings-project", semver = semver),
                SubprojectProject(projectName = "subproject-project", semver = semver),
            ),
        )
        test("when build metadata is invalid") {
            // Given
            projects.git {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")
                }
            }

            // When
            projects.build(GradleVersion.current())

            // Then
            projects.versions shouldOnlyHave "1.0.1"
        }
    }

    context("should append build metadata") {
        context("when ${BuildMetadataOptions.ALWAYS} is specified") {
            val semver = semver {
                appendBuildMetadata = BuildMetadataOptions.ALWAYS.name
            }

            val projects = install(
                GradleProjectsExtension(
                    RegularProject(projectName = "regular-project", semver = semver),
                    SettingsProject(projectName = "settings-project", semver = semver),
                    SubprojectProject(projectName = "subproject-project", semver = semver),
                ),
            )

            test("and on $mainBranch branch") {
                // Given
                projects.git {
                    initialBranch = mainBranch
                    actions = actions {
                        commit(message = "1 commit on $mainBranch", tag = "1.0.0")
                    }
                }

                // When
                projects.build(GradleVersion.current())

                // Then
                projects.versions shouldOnlyMatch """1.0.1\+[0-9]{12}""".toRegex()
            }

            test("and on $developmentBranch branch") {
                // Given
                projects.git {
                    initialBranch = mainBranch
                    actions = actions {
                        commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                        checkout(developmentBranch)
                        commit(message = "1 commit on $developmentBranch")
                    }
                }

                // When
                projects.build(GradleVersion.current())

                // Then
                projects.versions shouldOnlyMatch """1.0.1-$developmentBranch.1\+[0-9]{12}""".toRegex()
            }

            test("and on $featureBranch branch") {
                // Given
                projects.git {
                    initialBranch = mainBranch
                    actions = actions {
                        commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                        checkout(featureBranch)
                        commit(message = "1 commit on $featureBranch")
                    }
                }

                // When
                projects.build(GradleVersion.current())

                // Then
                projects.versions shouldOnlyMatch """1.0.1-${featureBranch.replace("/", "-")}.1\+[0-9]{12}""".toRegex()
            }
        }

        context("when ${BuildMetadataOptions.LOCALLY} is specified") {
            val semver = semver {
                appendBuildMetadata = BuildMetadataOptions.LOCALLY.name
            }

            val projects = install(
                GradleProjectsExtension(
                    RegularProject(projectName = "regular-project", semver = semver),
                    SettingsProject(projectName = "settings-project", semver = semver),
                    SubprojectProject(projectName = "subproject-project", semver = semver),
                ),
            )

            beforeAny {
                System.clearProperty("CI")
            }

            test("and on $mainBranch branch") {
                // Given
                projects.git {
                    initialBranch = mainBranch
                    actions = actions {
                        commit(message = "1 commit on $mainBranch", tag = "1.0.0")
                    }
                }

                // When
                projects.build(GradleVersion.current())

                // Then
                projects.versions shouldOnlyMatch """1.0.1\+[0-9]{12}""".toRegex()
            }

            test("and on $developmentBranch branch") {
                // Given
                projects.git {
                    initialBranch = mainBranch
                    actions = actions {
                        commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                        checkout(developmentBranch)
                        commit(message = "1 commit on $developmentBranch")
                    }
                }

                // When
                projects.build(GradleVersion.current())

                // Then
                projects.versions shouldOnlyMatch """1.0.1-$developmentBranch.1\+[0-9]{12}""".toRegex()
            }

            test("and on $featureBranch branch") {
                // Given
                projects.git {
                    initialBranch = mainBranch
                    actions = actions {
                        commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                        checkout(featureBranch)
                        commit(message = "1 commit on $featureBranch")
                    }
                }

                // When
                projects.build(GradleVersion.current())

                // Then
                projects.versions shouldOnlyMatch """1.0.1-${featureBranch.replace("/", "-")}.1\+[0-9]{12}""".toRegex()
            }
        }
    }
})
