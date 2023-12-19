package io.github.tcrawford.versioning.specs

import io.github.tcrawford.versioning.internal.properties.SemverProperty
import io.github.tcrawford.versioning.internal.properties.Stage
import io.github.tcrawford.versioning.kotest.GradleProjectsExtension
import io.github.tcrawford.versioning.kotest.shouldOnlyContain
import io.github.tcrawford.versioning.projects.RegularProject
import io.github.tcrawford.versioning.projects.SettingsProject
import io.github.tcrawford.versioning.projects.SubprojectProject
import io.github.tcrawford.versioning.util.GradleArgs.semverStage
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import org.gradle.util.GradleVersion

class CalculateNextVersionWithStageSpec : FunSpec({
    val projects = install(
        GradleProjectsExtension(
            RegularProject(projectName = "regular-project"),
            SettingsProject(projectName = "settings-project"),
            SubprojectProject(projectName = "subproject-project"),
        ),
    )

    val mainBranch = "main"
    val featureBranch = "feature/cool/branch"
    val developmentBranch = "dev"

    context("should not calculate next version") {
        test("when stage is invalid") {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")

                    checkout(mainBranch)
                }
            }

            // When
            val results = projects.runWithoutExpectations(GradleVersion.current(), "-P${SemverProperty.Stage.property}=invalid")

            // Then
            results.values.map { it.output } shouldOnlyContain "BUILD FAILED"
        }
    }

    context("should calculate next version with stage input") {
        test("on feature branch - new alpha version") {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                    checkout(featureBranch)
                    commit(message = "1 commit on $featureBranch")
                }
            }

            // When
            projects.build(GradleVersion.current(), semverStage(Stage.Alpha))

            // Then
            projects.versions shouldOnlyContain "1.0.1-alpha.1"
        }

        test("on feature branch - next alpha version") {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "1.0.2-alpha.1")

                    checkout(featureBranch)
                    commit(message = "1 commit on $featureBranch")
                }
            }

            // When
            projects.build(GradleVersion.current(), semverStage(Stage.Alpha))

            // Then
            projects.versions shouldOnlyContain "1.0.2-alpha.2"
        }

        test("on feature branch - new rc version") {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "1.0.2-alpha.3")

                    checkout(featureBranch)
                    commit(message = "1 commit on $featureBranch")
                }
            }

            // When
            projects.build(GradleVersion.current(), semverStage(Stage.ReleaseCandidate))

            // Then
            projects.versions shouldOnlyContain "1.0.3-rc.1"
        }

        test("on develop branch - new release version") {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "1.0.2-rc.5")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")
                }
            }

            // When
            projects.build(GradleVersion.current(), semverStage(Stage.Release))

            // Then
            projects.versions shouldOnlyContain "1.0.3-release.1"
        }

        test("on develop branch - new final version where last tag is from a branch") {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "1.0.2")
                    commit(message = "2 commit on $mainBranch", tag = "1.0.3-my-awesome-feature.5")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")
                }
            }

            // When
            projects.build(GradleVersion.current(), semverStage(Stage.Final))

            // Then
            projects.versions shouldOnlyContain "1.0.3-final.1"
        }

        test("on main branch - next stable version where last tag is from a branch") {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "1.0.2")
                    commit(message = "2 commit on $mainBranch", tag = "1.0.3-my-awesome-feature.5")
                }
            }

            // When
            projects.build(GradleVersion.current(), semverStage(Stage.Stable))

            // Then
            projects.versions shouldOnlyContain "1.0.3"
        }
    }
})
