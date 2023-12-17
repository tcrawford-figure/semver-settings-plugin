package io.github.tcrawford.versioning.specs

import io.github.tcrawford.versioning.git.Script
import io.github.tcrawford.versioning.internal.command.GitState
import io.github.tcrawford.versioning.kotest.GradleProjectsContainer
import io.github.tcrawford.versioning.kotest.shouldOnlyContain
import io.github.tcrawford.versioning.projects.RegularProject
import io.github.tcrawford.versioning.projects.SettingsProject
import io.github.tcrawford.versioning.projects.SubprojectProject
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import org.gradle.util.GradleVersion

class CalculateNextVersionInNonNominalState : FunSpec({
    val projects = install(GradleProjectsContainer) {
        projects += listOf(
            RegularProject(projectName = "regular-project"),
            SettingsProject(projectName = "settings-project"),
            SubprojectProject(projectName = "subproject-project"),
        )
    }

    context("should calculate next version when") {
        val mainBranch = "main"
        val featureBranch = "feature-branch"

        withData(
            nameFn = { "running ${it.script.scriptFileName}" },
            TestScriptData(Script.CREATE_BISECTING_STATE, "0.2.6-${GitState.BISECTING.description}"),
            TestScriptData(Script.CREATE_CHERRY_PICKING_STATE, "0.2.6-${GitState.CHERRY_PICKING.description}"),
            TestScriptData(Script.CREATE_MERGING_STATE, "0.2.6-${GitState.MERGING.description}"),
            TestScriptData(Script.CREATE_REBASING_STATE, "0.2.6-${GitState.REBASING.description}"),
            TestScriptData(Script.CREATE_REVERTING_STATE, "0.2.6-${GitState.REVERTING.description}"),
            TestScriptData(Script.CREATE_DETACHED_HEAD_STATE, "0.2.6-${GitState.DETACHED_HEAD.description}"),
        ) {
            // Given
            projects.install {
                initialBranch = mainBranch
                actions = actions {
                    commit(message = "1 commit on $mainBranch", tag = "0.2.5")
                    runScript(it.script, mainBranch, featureBranch)
                }
            }

            // When
            projects.build(GradleVersion.current())

            // Then
            projects.versions shouldOnlyContain it.expectedVersion
        }
    }
})

private data class TestScriptData(
    val script: Script,
    val expectedVersion: String,
)
