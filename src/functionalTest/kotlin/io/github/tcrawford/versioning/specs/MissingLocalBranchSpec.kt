package io.github.tcrawford.versioning.specs

import io.github.tcrawford.versioning.kotest.GradleProjectsExtension
import io.github.tcrawford.versioning.kotest.shouldOnlyHave
import io.github.tcrawford.versioning.projects.RegularProject
import io.github.tcrawford.versioning.projects.SettingsProject
import io.github.tcrawford.versioning.projects.SubprojectProject
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import org.gradle.util.GradleVersion

class MissingLocalBranchSpec : FunSpec({
    val projects = install(
        GradleProjectsExtension(
            RegularProject(projectName = "regular-project"),
            SettingsProject(projectName = "settings-project"),
            SubprojectProject(projectName = "subproject-project"),
        ),
    )

    val mainBranch = "master"
    val developmentBranch = "dev"
    val featureBranch = "feature-3"

    test("on development branch, missing local main branch") {
        // Given
        projects.install {
            initialBranch = mainBranch
            actions = actions {
                commit(message = "1 commit on $mainBranch", tag = "0.2.5")

                checkout(developmentBranch)
                commit(message = "1 commit on $developmentBranch")

                removeLocalBranch(mainBranch)
            }
        }

        // When
        projects.build(GradleVersion.current())

        // Then
        projects.versions shouldOnlyHave "0.2.6-dev.1"
    }

    test("on feature branch, missing local development branch") {
        // Given
        projects.install {
            initialBranch = mainBranch
            debugging = true
            actions = actions {
                commit(message = "1 commit on $mainBranch", tag = "0.2.5")

                checkout(developmentBranch)
                commit(message = "1 commit on $developmentBranch")

                checkout(featureBranch)
                commit(message = "1 commit on $featureBranch")

                removeLocalBranch(developmentBranch)
            }
        }

        // When
        projects.build(GradleVersion.current())

        // Then
        projects.versions shouldOnlyHave "0.2.6-feature-3.1"
    }
})
