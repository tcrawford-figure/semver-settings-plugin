package io.github.tcrawford.versioning.integration

import io.github.tcrawford.versioning.reader.fetchVersion
import io.github.tcrawford.versioning.testkit.GradleProjectListener
import io.github.tcrawford.versioning.testkit.repositoryConfig
import io.github.tcrawford.versioning.util.GradleArgs
import io.github.tcrawford.versioning.util.GradleArgs.semverOverrideVersion
import io.github.tcrawford.versioning.util.resolveResource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class UseOverrideVersionSpec : FunSpec({
    val gradleProjectListener = GradleProjectListener(resolveResource("settings-sample"))

    val defaultArguments = listOf(
        GradleArgs.STACKTRACE,
        GradleArgs.PARALLEL,
        GradleArgs.BUILD_CACHE,
        GradleArgs.CONFIGURATION_CACHE,
        GradleArgs.semverForTesting(true),
    )

    listener(gradleProjectListener)

    val mainBranch = "main"
    val developmentBranch = "develop"
    val featureBranch = "patch-1"

    context("should not use override version") {
        test("when override version is invalid") {
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

    context("should use override version") {
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
    }
})
