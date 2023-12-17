package io.github.tcrawford.versioning.specs

import io.github.tcrawford.versioning.kotest.GradleProjectsContainer
import io.github.tcrawford.versioning.kotest.shouldOnlyContain
import io.github.tcrawford.versioning.projects.RegularProject
import io.github.tcrawford.versioning.projects.SettingsProject
import io.github.tcrawford.versioning.projects.SubprojectProject
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import org.gradle.util.GradleVersion

class CalculateNextVersionWithoutInputsSpec : FunSpec({
    val projects = install(GradleProjectsContainer) {
        projects += listOf(
            RegularProject(projectName = "regular-project"),
            SettingsProject(projectName = "settings-project"),
            SubprojectProject(projectName = "subproject-project"),
        )
    }

    context("should calculate next version without inputs") {
        val mainBranch = "main"
        val developmentBranch = "develop"
        val featureBranch = "myname/sc-123456/my-awesome-feature"

        test("on main branch") {
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
            projects.build(GradleVersion.current())

            // Then
            projects.versions shouldOnlyContain "1.0.1"
        }

        test("on development branch") {
            // Given
            projects.install {
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
            projects.versions shouldOnlyContain "1.0.1-develop.1"
        }

        test("on development branch with latest development tag") {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")
                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch", tag = "1.0.1-develop.1")
                    commit(message = "2 commit on $developmentBranch")
                    commit(message = "3 commit on $developmentBranch")
                }
            }

            // When
            projects.build(GradleVersion.current())

            // Then
            projects.versions shouldOnlyContain "1.0.1-develop.3"
        }

        test("on feature branch off development branch") {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")

                    checkout(featureBranch)
                    commit(message = "1 commit on $featureBranch")
                    commit(message = "2 commit on $featureBranch")
                }
            }

            // When
            projects.build(GradleVersion.current())

            // Then
            projects.versions shouldOnlyContain "1.0.1-myname-sc-123456-my-awesome-feature.2"
        }

        test("on feature branch off main branch") {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                    checkout(featureBranch)
                    commit(message = "1 commit on $featureBranch")
                    commit(message = "2 commit on $featureBranch")
                }
            }

            // When
            projects.build(GradleVersion.current())

            // Then
            projects.versions shouldOnlyContain "1.0.1-myname-sc-123456-my-awesome-feature.2"
        }

        test("for develop branch after committing to feature branch and switching back to develop") {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")

                    checkout(featureBranch)
                    commit(message = "1 commit on $featureBranch")

                    checkout(developmentBranch)
                }
            }

            // When
            projects.build(GradleVersion.current())

            // Then
            projects.versions shouldOnlyContain "1.0.1-develop.1"
        }

        test("next branch version where last tag is prerelease") {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "1.0.2")
                    commit(message = "1 commit on $mainBranch", tag = "1.0.3-alpha.1")

                    checkout(featureBranch)
                    commit(message = "1 commit on $featureBranch")
                }
            }

            // When
            projects.build(GradleVersion.current())

            // Then
            projects.versions shouldOnlyContain "1.0.3-myname-sc-123456-my-awesome-feature.1"
        }
    }
})
