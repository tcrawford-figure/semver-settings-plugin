package io.github.tcrawford.gradle.semver.integration

import io.github.tcrawford.gradle.semver.reader.fetchVersion
import io.github.tcrawford.gradle.semver.testkit.GradleProjectListener
import io.github.tcrawford.gradle.semver.testkit.repositoryConfig
import io.github.tcrawford.gradle.semver.util.GradleArgs
import io.github.tcrawford.gradle.semver.util.resolveResource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MissingRemoteBranchSpec : FunSpec({
    val gradleProjectListener = GradleProjectListener(resolveResource("settings-sample"))

    val defaultArguments = listOf(
        GradleArgs.STACKTRACE,
        GradleArgs.PARALLEL,
        GradleArgs.BUILD_CACHE,
        GradleArgs.CONFIGURATION_CACHE,
        GradleArgs.semverForTesting(true)
    )

    listener(gradleProjectListener)

    context("when missing remote branch") {
        val mainBranch = "main"
        val developmentBranch = "develop"
        val featureBranch = "feature-2"

        test("on main, missing remote main branch") {
            // Given
            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "0.2.5")
                }
            }

            val runner = gradleProjectListener
                .initRepository(config, printLocalGitObjects = false) // defer logging of local git objects
                .initGradleRunner()
                .withArguments(defaultArguments)

            gradleProjectListener.removeRemoteBranch(mainBranch)

            // When
            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "0.2.6"
        }

        test("on develop, missing remote main branch") {
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

            gradleProjectListener.removeRemoteBranch(mainBranch)

            // When
            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "0.2.6-develop.1"
        }

        test("on feature branch, missing remote main branch") {
            // Given
            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "0.2.5")

                    checkout(featureBranch)
                    commit(message = "1 commit on $featureBranch")
                }
            }

            val runner = gradleProjectListener
                .initRepository(config, printLocalGitObjects = false) // defer logging of local git objects
                .initGradleRunner()
                .withArguments(defaultArguments)

            gradleProjectListener.removeRemoteBranch(mainBranch)

            // When
            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "0.2.6-feature-2.1"
        }
    }
})
