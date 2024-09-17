package io.github.tcrawford.versioning.specs

import io.github.tcrawford.versioning.gradle.build
import io.github.tcrawford.versioning.kotest.GradleProjectsExtension
import io.github.tcrawford.versioning.kotest.shouldOnlyHave
import io.github.tcrawford.versioning.projects.RegularProject
import io.github.tcrawford.versioning.projects.SettingsProject
import io.github.tcrawford.versioning.projects.SubprojectProject
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.gradle.util.GradleVersion

class UseInitialVersionSpec : FunSpec({
    // val projects = install(
    //     GradleProjectsExtension(
    //         RegularProject(projectName = "regular-project"),
    //         SettingsProject(projectName = "settings-project"),
    //         SubprojectProject(projectName = "subproject-project"),
    //     ),
    // )

    val mainBranch = "main"
    val developmentBranch = "develop"
    val featureBranch = "patch-1"

    context("should use initial version") {
        test("on main branch") {
            // Given
            val project = RegularProject("reg")
            project.git {
                initialBranch = mainBranch
                actions = actions {
                    commit("1 commit on $mainBranch")
                }
            }

            // When
            build(GradleVersion.current(), project.gradleProject.rootDir)

            // Then
            project.version shouldBe "0.0.1"
        }

        // test("on main branch") {
        //     // Given
        //     // The default initial value is "0.0.0" which is supplied by the plugin
        //     projects.git {
        //         initialBranch = mainBranch
        //         actions = actions {
        //             commit("1 commit on $mainBranch")
        //         }
        //     }
        //
        //     // When
        //     projects.build(GradleVersion.current())
        //
        //     // Then
        //     projects.versions shouldOnlyHave "0.0.1"
        // }
        //
        // test("on development branch") {
        //     // Given
        //     // The default initial value is "0.0.0" which is supplied by the plugin
        //     projects.git {
        //         initialBranch = mainBranch
        //         actions = actions {
        //             commit("1 commit on $mainBranch")
        //
        //             checkout(developmentBranch)
        //             commit("1 commit on $developmentBranch")
        //         }
        //     }
        //
        //     // When
        //     projects.build(GradleVersion.current())
        //
        //     // Then
        //     projects.versions shouldOnlyHave "0.0.1-develop.1"
        // }
        //
        // test("on feature branch") {
        //     // Given
        //     // The default initial value is "0.0.0" which is supplied by the plugin
        //     projects.git {
        //         initialBranch = mainBranch
        //         actions = actions {
        //             commit("1 commit on $mainBranch")
        //
        //             checkout(featureBranch)
        //             commit("1 commit on $featureBranch")
        //         }
        //     }
        //
        //     // When
        //     projects.build(GradleVersion.current())
        //
        //     // Then
        //     projects.versions shouldOnlyHave "0.0.1-patch-1.1"
        // }
    }
})
