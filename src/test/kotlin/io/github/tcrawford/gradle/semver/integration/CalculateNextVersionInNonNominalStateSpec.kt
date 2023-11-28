package io.github.tcrawford.gradle.semver.integration

import io.github.tcrawford.gradle.semver.internal.command.GitState
import io.github.tcrawford.gradle.semver.reader.fetchVersion
import io.github.tcrawford.gradle.semver.testkit.GradleProjectListener
import io.github.tcrawford.gradle.semver.testkit.repositoryConfig
import io.github.tcrawford.gradle.semver.util.GradleArgs
import io.github.tcrawford.gradle.semver.util.resolveResource
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class CalculateNextVersionInNonNominalStateSpec : FunSpec({
    val gradleProjectListener = GradleProjectListener(resolveResource("sample"))

    val defaultArguments = listOf(
        GradleArgs.STACKTRACE,
        GradleArgs.PARALLEL,
        GradleArgs.BUILD_CACHE,
        GradleArgs.CONFIGURATION_CACHE,
        GradleArgs.semverForTesting(true)
    )

    listener(gradleProjectListener)

    context("should calculate next version when") {
        val mainBranch = "main"
        val featureBranch = "feature-branch"

        withData(
            nameFn = { "running ${it.scriptFileName}" },
            TestData("create_bisecting_state.sh", "0.2.6-${GitState.BISECTING.description}"),
            TestData("create_cherry_picking_state.sh", "0.2.6-${GitState.CHERRY_PICKING.description}"),
            TestData("create_merging_state.sh", "0.2.6-${GitState.MERGING.description}"),
            TestData("create_rebasing_state.sh", "0.2.6-${GitState.REBASING.description}"),
            TestData("create_reverting_state.sh", "0.2.6-${GitState.REVERTING.description}"),
            TestData("create_detached_head_state.sh", "0.2.6-${GitState.DETACHED_HEAD.description}")
        ) {
            // Given
            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch$", tag = "0.2.5")
                    runScript(
                        script = resolveResource(resourcePath = "scripts/${it.scriptFileName}"),
                        gradleProjectListener.projectDir.absolutePath,
                        mainBranch,
                        featureBranch
                    )
                }
            }

            // When
            val runner = gradleProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(defaultArguments)

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe it.expectedVersion
        }
    }
})

private data class TestData(
    val scriptFileName: String,
    val expectedVersion: String
)
