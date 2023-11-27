package io.github.tcrawford.gradle.semver.integration

import io.github.tcrawford.gradle.semver.internal.Modifier
import io.github.tcrawford.gradle.semver.reader.fetchVersion
import io.github.tcrawford.gradle.semver.testkit.GradleProjectListener
import io.github.tcrawford.gradle.semver.testkit.repositoryConfig
import io.github.tcrawford.gradle.semver.util.GradleArgs
import io.github.tcrawford.gradle.semver.util.GradleArgs.semverModifier
import io.github.tcrawford.gradle.semver.util.resolveResource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CalculateNextVersionWithModifierSpec : FunSpec({
    val gradleProjectListener = GradleProjectListener(resolveResource("sample"))

    val defaultArguments = listOf(
        GradleArgs.Stacktrace,
        GradleArgs.Parallel,
        GradleArgs.BuildCache,
        GradleArgs.ConfigurationCache,
        GradleArgs.semverForTesting(true)
    )

    listener(gradleProjectListener)

    context("should calculate next version with modifier input") {
        val mainBranch = "master"
        val developmentBranch = "devel"
        val featureBranch = "cool-feature"

        test("on main branch - next major version") {
            // Given
            val arguments = defaultArguments + semverModifier(Modifier.Major)

            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")

                    checkout(mainBranch)
                }
            }

            // When
            val runner = gradleProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(arguments)

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "2.0.0"
        }

        test("on main branch - next minor version") {
            // Given
            val arguments = defaultArguments + semverModifier(Modifier.Minor)

            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")

                    checkout(mainBranch)
                }
            }

            // When
            val runner = gradleProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(arguments)

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.1.0"
        }

        test("on main branch - next patch version") {
            // Given
            val arguments = defaultArguments + semverModifier(Modifier.Patch)

            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")

                    checkout(mainBranch)
                }
            }

            // When
            val runner = gradleProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(arguments)

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.0.1"
        }

        test("on feature branch - next major version") {
            // Given
            val arguments = defaultArguments + semverModifier(Modifier.Major)

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
            gradleProjectListener.projectDir.fetchVersion() shouldBe "2.0.0-cool-feature.1"
        }

        test("on feature branch - next minor version") {
            // Given
            val arguments = defaultArguments + semverModifier(Modifier.Minor)

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
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.1.0-cool-feature.1"
        }

        test("on feature branch - next patch version") {
            // Given
            val arguments = defaultArguments + semverModifier(Modifier.Patch)

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
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.0.1-cool-feature.1"
        }
    }
})
