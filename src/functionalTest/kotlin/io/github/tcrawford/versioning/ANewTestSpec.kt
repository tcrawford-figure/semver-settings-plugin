package io.github.tcrawford.versioning

import io.github.tcrawford.versioning.projects.GradleProjects.Companion.gradleProjects
import io.github.tcrawford.versioning.projects.RegularProject
import io.github.tcrawford.versioning.projects.SettingsProject
import io.github.tcrawford.versioning.projects.SubprojectProject
import io.github.tcrawford.versioning.utils.shouldOnlyContain
import io.kotest.core.spec.style.FunSpec
import org.gradle.util.GradleVersion

class ANewTestSpec : FunSpec({
    val projects = gradleProjects(
        RegularProject(projectName = "regular-project"),
        SettingsProject(projectName = "settings-project"),
        SubprojectProject(projectName = "subproject-project"),
    )

    val mainBranch = "main"
    val featureBranch = "feature-branch"

    afterAny {
        projects.cleanGitDir()
    }

    test("test should run") {
        // Given
        projects.install {
            initialBranch = mainBranch
            actions = actions {
                commit(message = "1 commit on $mainBranch")
                commit(message = "2 commit on $mainBranch", tag = "1.0.0")
                checkout("feature-branch")
                commit(message = "1 commit on $featureBranch")
            }
        }

        // When
        projects.build(GradleVersion.current())

        // Then
        projects.versions shouldOnlyContain "1.0.1-$featureBranch.1"
    }
})
