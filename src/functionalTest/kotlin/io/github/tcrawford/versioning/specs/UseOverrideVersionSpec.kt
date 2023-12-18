package io.github.tcrawford.versioning.specs

import io.github.tcrawford.versioning.kotest.GradleProjectsExtension
import io.github.tcrawford.versioning.kotest.shouldOnlyContain
import io.github.tcrawford.versioning.kotest.shouldOnlyHave
import io.github.tcrawford.versioning.projects.RegularProject
import io.github.tcrawford.versioning.projects.SettingsProject
import io.github.tcrawford.versioning.projects.SubprojectProject
import io.github.tcrawford.versioning.util.GradleArgs.semverOverrideVersion
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import org.gradle.util.GradleVersion

class UseOverrideVersionSpec : FunSpec({
    val projects = install(
        GradleProjectsExtension(
            RegularProject(projectName = "regular-project"),
            SettingsProject(projectName = "settings-project"),
            SubprojectProject(projectName = "subproject-project"),
        ),
    )

    val mainBranch = "main"
    val developmentBranch = "develop"
    val featureBranch = "patch-1"

    context("should not use override version") {
        test("when override version is invalid") {
            // Given
            val givenVersion = "im-not-a-version"

            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch")
                }
            }

            // When
            val results = projects.runWithoutExpectations(GradleVersion.current(), semverOverrideVersion(givenVersion))

            // Then
            results.values.map { it.output } shouldOnlyContain "BUILD FAILED"
        }
    }

    context("should use override version") {
        test("on main branch") {
            // Given
            val givenVersion = "9.9.9"

            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch")
                }
            }

            // When
            projects.build(GradleVersion.current(), semverOverrideVersion(givenVersion))

            // Then
            projects.versions shouldOnlyHave givenVersion
        }

        test("on development branch") {
            // Given
            val givenVersion = "9.9.9-beta.1"

            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")
                }
            }

            // When
            projects.build(GradleVersion.current(), semverOverrideVersion(givenVersion))

            // Then
            projects.versions shouldOnlyHave givenVersion
        }

        test("on feature branch") {
            // Given
            val givenVersion = "9.9.9-alpha.1"

            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch")

                    checkout(featureBranch)
                    commit(message = "1 commit on $featureBranch")
                }
            }

            // When
            projects.build(GradleVersion.current(), semverOverrideVersion(givenVersion))

            // Then
            projects.versions shouldOnlyHave givenVersion
        }
    }
})
