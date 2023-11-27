package io.github.tcrawford.gradle.semver.integration

import io.github.tcrawford.gradle.semver.reader.fetchVersion
import io.github.tcrawford.gradle.semver.testkit.GradleProjectListener
import io.github.tcrawford.gradle.semver.testkit.repositoryConfig
import io.github.tcrawford.gradle.semver.util.GradleArgs
import io.github.tcrawford.gradle.semver.util.GradleArgs.semverOverrideVersion
import io.github.tcrawford.gradle.semver.util.resolveResourceDirectory
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class UseOverrideVersion : FunSpec({
    val gradleProjectListener = GradleProjectListener(resolveResourceDirectory("sample"))

    val defaultArguments = listOf(
        GradleArgs.Stacktrace,
        GradleArgs.Parallel,
        GradleArgs.BuildCache,
        GradleArgs.ConfigurationCache,
        GradleArgs.semverForTesting(true)
    )

    listener(gradleProjectListener)

    context("should use override version") {
        val mainBranch = "main"
        val developmentBranch = "develop"
        val featureBranch = "patch-1"

        test("on main branch") {
            // Given
            val givenVersion = "9.9.9"

            val arguments = defaultArguments + semverOverrideVersion(givenVersion)

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
                .withArguments(arguments)

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe givenVersion
        }

        test("on development branch") {
            // Given
            val givenVersion = "9.9.9-beta.1"
            val arguments = defaultArguments + semverOverrideVersion(givenVersion)

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
                .withArguments(arguments)

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe givenVersion
        }

        test("on feature branch") {
            // Given
            val givenVersion = "9.9.9-alpha.1"
            val arguments = defaultArguments + semverOverrideVersion(givenVersion)

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
                .withArguments(arguments)

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe givenVersion
        }

        test("is invalid version") {
            // Given
            val givenVersion = "im-not-a-version"
            val arguments = defaultArguments + semverOverrideVersion(givenVersion)

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
                .withArguments(arguments)

            val result = runner.run()

            // Then
            result.output shouldContain "BUILD FAILED"
        }
    }
})
