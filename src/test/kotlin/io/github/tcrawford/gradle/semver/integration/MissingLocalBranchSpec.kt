package io.github.tcrawford.gradle.semver.integration

import io.github.tcrawford.gradle.semver.reader.fetchVersion
import io.github.tcrawford.gradle.semver.testkit.GradleProjectListener
import io.github.tcrawford.gradle.semver.testkit.repositoryConfig
import io.github.tcrawford.gradle.semver.util.GradleArgs
import io.github.tcrawford.gradle.semver.util.GradleArgs.semverForTesting
import io.github.tcrawford.gradle.semver.util.resolveResource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MissingLocalBranchSpec : FunSpec({
    val gradleProjectListener = GradleProjectListener(resolveResource("sample"))

    val defaultArguments = listOf(
        GradleArgs.Stacktrace,
        GradleArgs.Parallel,
        GradleArgs.BuildCache,
        GradleArgs.ConfigurationCache,
        semverForTesting(true)
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
