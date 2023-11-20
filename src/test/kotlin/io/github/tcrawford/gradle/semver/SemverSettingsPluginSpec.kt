package io.github.tcrawford.gradle.semver

import io.github.tcrawford.gradle.semver.internal.Modifier
import io.github.tcrawford.gradle.semver.internal.Stage
import io.github.tcrawford.gradle.semver.reader.fetchVersion
import io.github.tcrawford.gradle.semver.testkit.GradleProjectListener
import io.github.tcrawford.gradle.semver.testkit.repositoryConfig
import io.github.tcrawford.gradle.semver.util.GradleArgs
import io.github.tcrawford.gradle.semver.util.GradleArgs.semverForTesting
import io.github.tcrawford.gradle.semver.util.GradleArgs.semverModifier
import io.github.tcrawford.gradle.semver.util.GradleArgs.semverStage
import io.github.tcrawford.gradle.semver.util.resolveResourceDirectory
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

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

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.0.1"
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

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.0.1-develop.1"
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

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.0.1-myname-sc-123456-my-awesome-feature.2"
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

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.0.1-myname-sc-123456-my-awesome-feature.2"
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

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.0.1-develop.1"
        }

        test("next branch version where last tag is prerelease") {
            // Given
            val config = repositoryConfig {
                initialBranch = mainBranch
                actions {
                    checkout(mainBranch)
                    commit(message = "1 commit on $mainBranch", tag = "1.0.2")
                    commit(message = "1 commit on $mainBranch", tag = "1.0.3-alpha.1")

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
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.0.3-myname-sc-123456-my-awesome-feature.1"
        }
    }

    context("should calculate next version with modifier input") {
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

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "2.0.0"
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

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.1.0"
        }

        test("on main branch - next patch version") {
            // Given
            val additionalArguments = listOf(
                semverModifier(Modifier.Patch),
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

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.0.1"
        }

        test("on feature branch - next major version") {
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

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.1.0-cool-feature.1"
        }

        test("on feature branch - next patch version") {
            // Given
            val additionalArguments = listOf(
                semverModifier(Modifier.Patch),
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

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.0.1-cool-feature.1"
        }
    }

    context("should calculate next version with stage input") {
        val mainBranch = "main"
        val featureBranch = "feature/cool/branch"
        val developmentBranch = "dev"

        test("on feature branch - new alpha version") {
            // Given
            val additionalArguments = listOf(
                semverStage(Stage.Alpha),
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

            runner.build()

            // Then
            gradleProjectListener.projectDir.fetchVersion() shouldBe "1.0.1-alpha.1"
        }

        test("on feature branch - next alpha version") {
            // Given
            val additionalArguments = listOf(
                semverStage(Stage.Alpha),
            )

            val arguments = defaultArguments + additionalArguments

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
            val additionalArguments = listOf(
                semverStage(Stage.RC),
            )

            val arguments = defaultArguments + additionalArguments

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
            val additionalArguments = listOf(
                semverStage(Stage.Release),
            )

            val arguments = defaultArguments + additionalArguments

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
            val additionalArguments = listOf(
                semverStage(Stage.Final),
            )

            val arguments = defaultArguments + additionalArguments

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
            val additionalArguments = listOf(
                semverStage(Stage.Stable),
            )

            val arguments = defaultArguments + additionalArguments

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

    context("should use initial version") {
        val mainBranch = "main"
        val developmentBranch = "develop"
        val featureBranch = "patch-1"

        test("on main branch - use initial version") {
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

        test("on development branch - use initial version") {
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

        test("on feature branch - use initial version") {
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
