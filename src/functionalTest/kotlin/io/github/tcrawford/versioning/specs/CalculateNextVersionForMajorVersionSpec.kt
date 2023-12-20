package io.github.tcrawford.versioning.specs

import io.github.tcrawford.versioning.internal.properties.Modifier
import io.github.tcrawford.versioning.internal.properties.SemverProperty
import io.github.tcrawford.versioning.internal.properties.Stage
import io.github.tcrawford.versioning.kotest.GradleProjectsExtension
import io.github.tcrawford.versioning.kotest.shouldOnlyContain
import io.github.tcrawford.versioning.kotest.shouldOnlyHave
import io.github.tcrawford.versioning.projects.RegularProject
import io.github.tcrawford.versioning.projects.SettingsProject
import io.github.tcrawford.versioning.projects.SubprojectProject
import io.github.tcrawford.versioning.util.GradleArgs.semverForMajorVersion
import io.github.tcrawford.versioning.util.GradleArgs.semverModifier
import io.github.tcrawford.versioning.util.GradleArgs.semverStage
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import org.gradle.util.GradleVersion

class CalculateNextVersionForMajorVersionSpec : FunSpec({
    val projects = install(
        GradleProjectsExtension(
            RegularProject(projectName = "regular-project"),
            SettingsProject(projectName = "settings-project"),
            SubprojectProject(projectName = "subproject-project"),
        ),
    )

    val mainBranch = "master"
    val developmentBranch = "devel"
    val featureBranch = "cool-feature"
    val releaseBranch = "release/v0"

    context("should not calculate next version for major version") {
        test("when value is not an integer") {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "0.2.5")
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")
                    commit(message = "1 commit on $mainBranch", tag = "1.0.1")
                    commit(message = "1 commit on $mainBranch", tag = "1.1.0")

                    checkout(releaseBranch)
                    commit(message = "1 commit on $releaseBranch")
                }
            }

            // When
            val outputs = projects.runWithoutExpectations(
                GradleVersion.current(),
                "-P${SemverProperty.ForMajorVersion.property}=not-an-integer",
            ).values.map { it.output }

            // Then
            outputs shouldOnlyContain "semver.forMajorVersion must be representative of a valid major version line (0, 1, 2, etc.)"
        }

        test("when modifier is major and next major version is specified") {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "0.2.5")
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")
                    commit(message = "1 commit on $mainBranch", tag = "1.0.1")
                    commit(message = "1 commit on $mainBranch", tag = "1.1.0")

                    checkout(releaseBranch)
                    commit(message = "1 commit on $releaseBranch")
                }
            }

            // When
            val results = projects.runWithoutExpectations(
                GradleVersion.current(),
                semverStage(Stage.Stable),
                semverModifier(Modifier.Major),
                semverForMajorVersion(0),
            )

            // Then
            results.values.map { it.output } shouldOnlyContain "forMajorVersion cannot be used with the 'major' modifier"
        }
    }

    context("should calculate next version for major version") {
        test("on main branch - next minor version") {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "0.2.5")
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")
                    commit(message = "1 commit on $mainBranch", tag = "1.0.1")
                    commit(message = "1 commit on $mainBranch", tag = "1.1.0")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")

                    checkout(mainBranch)
                }
            }

            // When
            projects.build(GradleVersion.current(), semverModifier(Modifier.Minor), semverForMajorVersion(0))

            // Then
            projects.versions shouldOnlyHave "0.3.0"
        }

        test("on main branch - next patch version") {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "0.2.5")
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")
                    commit(message = "1 commit on $mainBranch", tag = "1.0.1")
                    commit(message = "1 commit on $mainBranch", tag = "1.1.0")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")

                    checkout(mainBranch)
                }
            }

            // When
            projects.build(GradleVersion.current(), semverForMajorVersion(0))

            // Then
            projects.versions shouldOnlyHave "0.2.6"
        }

        test("on development branch - next patch version") {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "0.2.5")
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")
                    commit(message = "1 commit on $mainBranch", tag = "1.0.1")
                    commit(message = "1 commit on $mainBranch", tag = "1.1.0")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")
                }
            }

            // When
            projects.build(GradleVersion.current(), semverStage(Stage.Stable), semverForMajorVersion(0))

            // Then
            projects.versions shouldOnlyHave "0.2.6"
        }

        test("on feature branch - next patch version") {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "0.2.5")
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")
                    commit(message = "1 commit on $mainBranch", tag = "1.0.1")
                    commit(message = "1 commit on $mainBranch", tag = "1.1.0")

                    checkout(featureBranch)
                    commit(message = "1 commit on $featureBranch")
                }
            }

            // When
            projects.build(GradleVersion.current(), semverStage(Stage.Stable), semverForMajorVersion(0))

            // Then
            projects.versions shouldOnlyHave "0.2.6"
        }

        test("on feature branch - new release candidate version") {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "0.2.5")
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")
                    commit(message = "1 commit on $mainBranch", tag = "1.0.1")
                    commit(message = "1 commit on $mainBranch", tag = "1.1.0")

                    checkout(featureBranch)
                    commit(message = "1 commit on $featureBranch")
                }
            }

            // When
            projects.build(GradleVersion.current(), semverStage(Stage.ReleaseCandidate), semverForMajorVersion(0))

            // Then
            projects.versions shouldOnlyHave "0.2.6-rc.1"
        }
    }
})
