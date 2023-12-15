package io.github.tcrawford.versioning.integration

import io.github.tcrawford.versioning.reader.fetchVersion
import io.github.tcrawford.versioning.reader.fetchVersionTag
import io.github.tcrawford.versioning.testkit.GradleProjectListener
import io.github.tcrawford.versioning.testkit.repositoryConfig
import io.github.tcrawford.versioning.util.GradleArgs
import io.github.tcrawford.versioning.util.resolveResource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class UseTagPrefixSpec : FunSpec({
    val gradleProjectListener = GradleProjectListener(resolveResource("settings-sample"))

    val defaultArguments = listOf(
        GradleArgs.STACKTRACE,
        GradleArgs.PARALLEL,
        GradleArgs.BUILD_CACHE,
        GradleArgs.CONFIGURATION_CACHE,
        GradleArgs.semverForTesting(true),
    )

    listener(gradleProjectListener)

    context("should use tag prefix") {
        val mainBranch = "main"

        test("on main branch with default tag prefix") {
            // Given
            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "0.2.5")
                }
            }

            // When
            val runner = gradleProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(defaultArguments)

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "0.2.6"
            gradleProjectListener.projectDir.fetchVersionTag() shouldBe "v0.2.6"
        }

        test("on main branch with provided tag prefix") {
            // Given
            val arguments = defaultArguments + GradleArgs.semverTagPrefix("Nov Release ")

            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "0.2.5")
                }
            }

            // When
            val runner = gradleProjectListener
                .initRepository(config)
                .initGradleRunner()
                .withArguments(arguments)

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "0.2.6"
            gradleProjectListener.projectDir.fetchVersionTag() shouldBe "Nov Release 0.2.6"
        }
    }
})
