package io.github.tcrawford.versioning.integration

import io.github.tcrawford.versioning.reader.fetchVersion
import io.github.tcrawford.versioning.testkit.GradleProjectListener
import io.github.tcrawford.versioning.testkit.repositoryConfig
import io.github.tcrawford.versioning.util.GradleArgs
import io.github.tcrawford.versioning.util.GradleArgs.semverForTesting
import io.github.tcrawford.versioning.util.resolveResource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MissingLocalBranchSpec : FunSpec({
    val gradleProjectListener = GradleProjectListener(resolveResource("settings-sample"))

    val defaultArguments = listOf(
        GradleArgs.STACKTRACE,
        GradleArgs.PARALLEL,
        GradleArgs.BUILD_CACHE,
        GradleArgs.CONFIGURATION_CACHE,
        semverForTesting(true),
    )

    listener(gradleProjectListener)

    context("when missing local branch") {
        val mainBranch = "master"
        val developmentBranch = "dev"
        val featureBranch = "feature-3"

        test("on development branch, missing remote main branch") {
            // Given
            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "0.2.5")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")
                }
            }

            val runner = gradleProjectListener
                .initRepository(config, printLocalGitObjects = false) // defer logging of local git objects
                .initGradleRunner()
                .withArguments(defaultArguments)

            gradleProjectListener.removeLocalBranch(mainBranch)

            // When
            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "0.2.6-dev.1"
        }

        test("on feature branch, missing remote development branch") {
            // Given
            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "0.2.5")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")

                    checkout(featureBranch)
                    commit(message = "1 commit on $featureBranch")
                }
            }

            val runner = gradleProjectListener
                .initRepository(config, printLocalGitObjects = false) // defer logging of local git objects
                .initGradleRunner()
                .withArguments(defaultArguments)

            gradleProjectListener.removeLocalBranch(developmentBranch)

            // When
            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "0.2.6-feature-3.1"
        }
    }
})
