package io.github.tcrawford.versioning.specs

import io.github.tcrawford.versioning.gradle.semver
import io.github.tcrawford.versioning.kotest.GradleProjectsExtension
import io.github.tcrawford.versioning.kotest.shouldOnlyHave
import io.github.tcrawford.versioning.projects.RegularProject
import io.github.tcrawford.versioning.projects.SettingsProject
import io.github.tcrawford.versioning.projects.SubprojectProject
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import org.gradle.util.GradleVersion

class UseDevelopmentBranchSpec : FunSpec({
    val mainBranch = "main"
    val developBranch = "staging"
    val featureBranch = "feature-1"

    context("should use development branch") {
        val semver = semver {
            this.mainBranch = mainBranch
        }

        val projects = install(
            GradleProjectsExtension(
                RegularProject(projectName = "regular-project", semver = semver),
                SettingsProject(projectName = "settings-project", semver = semver),
                SubprojectProject(projectName = "subproject-project", semver = semver),
            ),
        )

        test("on $developBranch branch") {
            // Given
            projects.git {
                initialBranch = mainBranch
                actions = actions {
                    commit("1 commit on $mainBranch", tag = "1.0.0")
                    checkout(developBranch)
                    commit("1 commit on $developBranch")
                }
            }
            // When
            projects.build(GradleVersion.current())

            // Then
            projects.versions shouldOnlyHave "1.0.1-$developBranch.1"
        }

        test("on $featureBranch branch") {
            // Given
            projects.git {
                initialBranch = mainBranch
                actions = actions {
                    commit("1 commit on $mainBranch", tag = "1.0.0")
                    checkout(developBranch)
                    commit("1 commit on $developBranch")
                    checkout(featureBranch)
                    commit("1 commit on $featureBranch")
                }
            }
            // When
            projects.build(GradleVersion.current())

            // Then
            projects.versions shouldOnlyHave "1.0.1-$featureBranch.2"
        }
    }
})
