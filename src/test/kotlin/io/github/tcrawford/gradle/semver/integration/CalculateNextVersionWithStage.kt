package io.github.tcrawford.gradle.semver.integration

import io.github.tcrawford.gradle.semver.internal.Stage
import io.github.tcrawford.gradle.semver.reader.fetchVersion
import io.github.tcrawford.gradle.semver.testkit.GradleProjectListener
import io.github.tcrawford.gradle.semver.testkit.repositoryConfig
import io.github.tcrawford.gradle.semver.util.GradleArgs
import io.github.tcrawford.gradle.semver.util.GradleArgs.semverStage
import io.github.tcrawford.gradle.semver.util.resolveResourceDirectory
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CalculateNextVersionWithStage : FunSpec({
    val gradleProjectListener = GradleProjectListener(resolveResourceDirectory("sample"))

    val defaultArguments = listOf(
        GradleArgs.Stacktrace,
        GradleArgs.Parallel,
        GradleArgs.BuildCache,
        GradleArgs.ConfigurationCache,
        GradleArgs.semverForTesting(true)
    )

    listener(gradleProjectListener)

    context("should calculate next version with stage input") {
        val mainBranch = "main"
        val featureBranch = "feature/cool/branch"
        val developmentBranch = "dev"

        test("on feature branch - new alpha version") {
            // Given
            val arguments = defaultArguments + semverStage(Stage.Alpha)

            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                    checkout(featureBranch)
                    commit(message = "1 commit on $featureBranch")
                }
            }

            // When
            val runner = gradleProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(arguments)

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.0.1-alpha.1"
        }

        test("on feature branch - next alpha version") {
            // Given
            val arguments = defaultArguments + semverStage(Stage.Alpha)

            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.2-alpha.1")

                    checkout(featureBranch)
                    commit(message = "1 commit on $featureBranch")
                }
            }

            // When
            val runner = gradleProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(arguments)

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.0.2-alpha.2"
        }

        test("on feature branch - new rc version") {
            // Given
            val arguments = defaultArguments + semverStage(Stage.RC)

            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.2-alpha.3")

                    checkout(featureBranch)
                    commit(message = "1 commit on $featureBranch")
                }
            }

            // When
            val runner = gradleProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(arguments)

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.0.3-rc.1"
        }

        test("on develop branch - new release version") {
            // Given
            val arguments = defaultArguments + semverStage(Stage.Release)

            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.2-rc.5")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")
                }
            }

            // When
            val runner = gradleProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(arguments)

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.0.3-release.1"
        }

        test("on develop branch - new final version where last tag is from a branch") {
            // Given
            val arguments = defaultArguments + semverStage(Stage.Final)

            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.2")
                    commit(message = "1 commit on $mainBranch", tag = "1.0.3-my-awesome-feature.5")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")
                }
            }

            // When
            val runner = gradleProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(arguments)

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.0.3-final.1"
        }

        test("on main branch - next stable version where last tag is from a branch") {
            // Given
            val arguments = defaultArguments + semverStage(Stage.Stable)

            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.2")
                    commit(message = "1 commit on $mainBranch", tag = "1.0.3-my-awesome-feature.5")
                }
            }

            // When
            val runner = gradleProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(arguments)

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.0.3"
        }
    }
})
