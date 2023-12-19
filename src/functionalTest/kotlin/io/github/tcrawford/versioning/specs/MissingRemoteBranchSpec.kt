package io.github.tcrawford.versioning.specs

import io.github.tcrawford.versioning.kotest.GradleProjectsExtension
import io.github.tcrawford.versioning.kotest.shouldOnlyHave
import io.github.tcrawford.versioning.projects.RegularProject
import io.github.tcrawford.versioning.projects.SettingsProject
import io.github.tcrawford.versioning.projects.SubprojectProject
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import org.gradle.util.GradleVersion

class MissingRemoteBranchSpec : FunSpec({
    val projects = install(
        GradleProjectsExtension(
            RegularProject(projectName = "regular-project"),
            SettingsProject(projectName = "settings-project"),
            SubprojectProject(projectName = "subproject-project"),
        ),
    )

    val mainBranch = "main"
    val developmentBranch = "develop"
    val featureBranch = "feature-2"

    test("on main, missing remote main branch") {
        // Given
        projects.install {
            initialBranch = mainBranch
            actions = actions {
                commit(message = "1 commit on $mainBranch", tag = "0.2.5")

                removeRemoteBranch(mainBranch)
            }
        }

        // When
        projects.build(GradleVersion.current())

        // Then
        projects.versions shouldOnlyHave "0.2.6"
    }

    test("on develop, missing remote main branch") {
        // Given
        projects.install {
            initialBranch = mainBranch
            actions = actions {
                commit(message = "1 commit on $mainBranch", tag = "0.2.5")

                checkout(developmentBranch)
                commit(message = "1 commit on $developmentBranch")

                removeRemoteBranch(mainBranch)
            }
        }

        // When
        projects.build(GradleVersion.current())

        // Then
        projects.versions shouldOnlyHave "0.2.6-develop.1"
    }

    test("on feature branch, missing remote main branch") {
        // Given
        projects.install {
            initialBranch = mainBranch
            actions = actions {
                commit(message = "1 commit on $mainBranch", tag = "0.2.5")

                checkout(featureBranch)
                commit(message = "1 commit on $featureBranch")

                removeRemoteBranch(mainBranch)
            }
        }

        // When
        projects.build(GradleVersion.current())

        // Then
        projects.versions shouldOnlyHave "0.2.6-feature-2.1"
    }
})
