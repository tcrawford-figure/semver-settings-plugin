package io.github.tcrawford.gradle.semver

import io.github.tcrawford.gradle.semver.internal.Modifier
import io.github.tcrawford.gradle.semver.testkit.GradleProjectListener
import io.github.tcrawford.gradle.semver.testkit.repositoryConfig
import io.github.tcrawford.gradle.semver.util.GradleArgs
import io.github.tcrawford.gradle.semver.util.GradleArgs.semverForTesting
import io.github.tcrawford.gradle.semver.util.GradleArgs.semverModifier
import io.github.tcrawford.gradle.semver.util.resolveResourceDirectory
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class SemverSettingsPluginSpec : FunSpec({

    val gradleProjectListener = GradleProjectListener(resolveResourceDirectory("sample"))

    val defaultArguments = listOf(
        GradleArgs.Stacktrace,
        GradleArgs.Parallel,
        GradleArgs.BuildCache,
        GradleArgs.ConfigurationCache,
        semverForTesting(true)
    )

    listener(gradleProjectListener)

    context("should calculate next version without inputs") {
        val mainBranch = "main"
        val developmentBranch = "develop"
        val featureBranch = "myname/sc-123456/my-awesome-feature"

        test("on main branch") {
            // Given
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
                .withArguments(defaultArguments)

            val buildResult = runner.build()

            // Then
            buildResult.output shouldContain "1.0.1"
        }

        test("on development branch") {
            // Given
            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")
                }
            }

            // When
            val runner = gradleProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(defaultArguments)

            val buildResult = runner.build()

            // Then
            buildResult.output shouldContain "1.0.1-develop.1"
        }

        test("on feature branch off development branch") {
            // Given
            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")

                    checkout(featureBranch)
                    commit(message = "1 commit on $developmentBranch")
                    commit(message = "2 commit on $developmentBranch")
                }
            }

            // When
            val runner = gradleProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(defaultArguments)

            val buildResult = runner.build()

            // Then
            buildResult.output shouldContain "1.0.1-myname-sc-123456-my-awesome-feature.2"
        }

        test("on feature branch off main branch") {
            // Given
            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                    checkout(featureBranch)
                    commit(message = "1 commit on $developmentBranch")
                    commit(message = "2 commit on $developmentBranch")
                }
            }

            // When
            val runner = gradleProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(defaultArguments)

            val buildResult = runner.build()

            // Then
            buildResult.output shouldContain "1.0.1-myname-sc-123456-my-awesome-feature.2"
        }

        test("for develop branch after committing to feature branch and switching back to develop") {
            // Given
            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.0")

                    checkout(developmentBranch)
                    commit(message = "1 commit on $developmentBranch")

                    checkout(featureBranch)
                    commit(message = "1 commit on $featureBranch")

                    checkout(developmentBranch)
                }
            }

            // When
            val runner = gradleProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(defaultArguments)

            val buildResult = runner.build()

            // Then
            buildResult.output shouldContain "1.0.1-develop.1"
        }
    }

    context("should calculate next version with inputs") {
        val mainBranch = "master"
        val developmentBranch = "devel"
        val featureBranch = "cool-feature"

        test("on main branch - next major version") {
            // Given
            val additionalArguments = listOf(
                semverModifier(Modifier.Major),
            )

            val arguments = defaultArguments + additionalArguments

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

            val buildResult = runner.build()

            // Then
            buildResult.output shouldContain "2.0.0"
        }

        test("on main branch - next minor version") {
            // Given
            val additionalArguments = listOf(
                semverModifier(Modifier.Minor),
            )

            val arguments = defaultArguments + additionalArguments

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

            val buildResult = runner.build()

            // Then
            buildResult.output shouldContain "1.1.0"
        }

        test("on feature branch - next minor version") {
            // Given
            val additionalArguments = listOf(
                semverModifier(Modifier.Minor),
            )

            val arguments = defaultArguments + additionalArguments

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

            val buildResult = runner.build()

            // Then
            buildResult.output shouldContain "1.1.0-cool-feature.1"
        }
    }
})
