package io.github.tcrawford.gradle.semver.integration

import io.github.tcrawford.gradle.semver.reader.fetchVersion
import io.github.tcrawford.gradle.semver.testkit.GradleProjectListener
import io.github.tcrawford.gradle.semver.testkit.repositoryConfig
import io.github.tcrawford.gradle.semver.util.GradleArgs
import io.github.tcrawford.gradle.semver.util.resolveResourceDirectory
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class UseInitialVersion : FunSpec({
    val gradleProjectListener = GradleProjectListener(resolveResourceDirectory("sample"))

    val defaultArguments = listOf(
        GradleArgs.Stacktrace,
        GradleArgs.Parallel,
        GradleArgs.BuildCache,
        GradleArgs.ConfigurationCache,
        GradleArgs.semverForTesting(true)
    )

    listener(gradleProjectListener)

    context("should use initial version") {
        val mainBranch = "main"
        val developmentBranch = "develop"
        val featureBranch = "patch-1"

        test("on main branch") {
            // Given
            // Note that the default initial value should be "0.0.0"

            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch")
                }
            }

            // When
            val runner = gradleProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(defaultArguments)

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "0.0.1"
        }

        test("on development branch") {
            // Given
            // Note that the default initial value should be "0.0.0"

            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")
                }
            }

            // When
            val runner = gradleProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(defaultArguments)

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "0.0.1-develop.1"
        }

        test("on feature branch") {
            // Given
            // Note that the default initial value should be "0.0.0"

            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch")

                    checkout(featureBranch)
                    commit(message = "1 commit on $featureBranch")
                }
            }

            // When
            val runner = gradleProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(defaultArguments)

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "0.0.1-patch-1.1"
        }
    }
})
