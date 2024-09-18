package io.github.tcrawford.versioning.specs

import io.github.tcrawford.versioning.gradle.semver
import io.github.tcrawford.versioning.kotest.GradleProjectsExtension
import io.github.tcrawford.versioning.kotest.shouldOnlyHave
import io.github.tcrawford.versioning.projects.RegularProject
import io.github.tcrawford.versioning.projects.SettingsProject
import io.github.tcrawford.versioning.projects.SubprojectProject
import io.github.z4kn4fein.semver.nextPatch
import io.github.z4kn4fein.semver.toVersion
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import org.gradle.util.GradleVersion

class UseInitialVersionSpec : FunSpec({
    val mainBranch = "main"
    val developmentBranch = "develop"
    val featureBranch = "patch-1"

    val initialVersion = "1.1.1"
    val expectedStableVersion = initialVersion.toVersion().nextPatch().toString()

    context("should use initial version") {
        val semver = semver {
            this.initialVersion = initialVersion
        }

        val projects = install(
            GradleProjectsExtension(
                RegularProject(projectName = "regular-project", semver = semver),
                SettingsProject(projectName = "settings-project", semver = semver),
                SubprojectProject(projectName = "subproject-project", semver = semver),
            ),
        )

        test("on main branch") {
            // Given
            // The default initial value is "0.0.0" which is supplied by the plugin
            projects.git {
                initialBranch = mainBranch
                actions = actions {
                    commit("1 commit on $mainBranch")
                }
            }

            // When
            projects.build(GradleVersion.current())

            // Then
            projects.versions shouldOnlyHave expectedStableVersion
        }

        test("on development branch") {
            // Given
            // The default initial value is "0.0.0" which is supplied by the plugin
            projects.git {
                initialBranch = mainBranch
                actions = actions {
                    commit("1 commit on $mainBranch")

                    checkout(developmentBranch)
                    commit("1 commit on $developmentBranch")
                }
            }

            // When
            projects.build(GradleVersion.current())

            // Then
            projects.versions shouldOnlyHave "$expectedStableVersion-develop.1"
        }

        test("on feature branch") {
            // Given
            // The default initial value is "0.0.0" which is supplied by the plugin
            projects.git {
                initialBranch = mainBranch
                actions = actions {
                    commit("1 commit on $mainBranch")

                    checkout(featureBranch)
                    commit("1 commit on $featureBranch")
                }
            }

            // When
            projects.build(GradleVersion.current())

            // Then
            projects.versions shouldOnlyHave "$expectedStableVersion-patch-1.1"
        }
    }
})
