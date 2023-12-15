package io.github.tcrawford.versioning.integration

import io.github.tcrawford.versioning.internal.properties.Modifier
import io.github.tcrawford.versioning.internal.properties.SemverProperty
import io.github.tcrawford.versioning.reader.fetchVersion
import io.github.tcrawford.versioning.testkit.GradleProjectListener
import io.github.tcrawford.versioning.testkit.repositoryConfig
import io.github.tcrawford.versioning.util.GradleArgs
import io.github.tcrawford.versioning.util.GradleArgs.semverModifier
import io.github.tcrawford.versioning.util.resolveResource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class CalculateNextVersionWithModifierSpec : FunSpec({
    val gradleProjectListener = GradleProjectListener(resolveResource("settings-sample"))

    val defaultArguments = listOf(
        GradleArgs.STACKTRACE,
        GradleArgs.PARALLEL,
        GradleArgs.BUILD_CACHE,
        GradleArgs.CONFIGURATION_CACHE,
        GradleArgs.semverForTesting(true),
    )

    listener(gradleProjectListener)

    val mainBranch = "master"
    val developmentBranch = "devel"
    val featureBranch = "cool-feature"

    context("should not calculate next version") {
        test("when modifier is invalid") {
            // Given
            val arguments = defaultArguments + "-P${SemverProperty.Modifier.property}=invalid"

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

            val result = runner.run()

            // Then
            result.output shouldContain "BUILD FAILED"
        }
    }

    context("should calculate next version with modifier input") {
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
