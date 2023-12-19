package io.github.tcrawford.versioning.internal.calculator

import io.github.tcrawford.versioning.internal.command.GitState
import io.github.tcrawford.versioning.internal.command.KGit
import io.github.tcrawford.versioning.internal.errors.InvalidOverrideVersionError
import io.github.tcrawford.versioning.internal.logging.warn
import io.github.tcrawford.versioning.internal.properties.Stage
import io.github.z4kn4fein.semver.nextPatch
import io.github.z4kn4fein.semver.toVersion
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters

private val log = Logging.getLogger(Logger.ROOT_LOGGER_NAME)

fun ProviderFactory.versionFactory(
    context: VersionFactoryContext,
): Provider<String> =
    of(VersionFactory::class.java) { spec ->
        spec.parameters {
            it.versionFactoryContext.set(context)
        }
    }

abstract class VersionFactory : ValueSource<String, VersionFactory.Params> {
    interface Params : ValueSourceParameters {
        val versionFactoryContext: Property<VersionFactoryContext>
    }

    private fun Params.toVersionCalculatorContext(gitState: GitState) =
        with(versionFactoryContext.get()) {
            VersionCalculatorContext(
                stage = stage,
                modifier = modifier,
                forTesting = forTesting,
                gitState = gitState,
                mainBranch = mainBranch,
                developmentBranch = developmentBranch,
            )
        }

    override fun obtain(): String {
        val factoryContext = parameters.versionFactoryContext.get()

        if (!factoryContext.rootDir.resolve(".git").exists()) {
            log.warn { "Git has not been initialized in this repository. Please initialize it with 'git init'." }
            val nextVersion = factoryContext.initialVersion.toVersion().nextPatch().toString()
            return "$nextVersion-UNINITIALIZED-REPO"
        }

        val kgit = KGit(directory = factoryContext.rootDir)

        val context = parameters.toVersionCalculatorContext(kgit.state())

        val overrideVersion = factoryContext.overrideVersion
        val latestVersion = kgit.tags.latestOrInitial(factoryContext.initialVersion)
        val latestNonPreReleaseVersion = kgit.tags.latestNonPreReleaseOrInitial(factoryContext.initialVersion)

        val version = when {
            context.gitState != GitState.NOMINAL -> {
                val gitStateVersionCalculator = GitStateVersionCalculator()
                gitStateVersionCalculator.calculate(latestNonPreReleaseVersion, context)
            }

            overrideVersion != null -> {
                runCatching { overrideVersion.toVersion() }.getOrElse {
                    throw InvalidOverrideVersionError(overrideVersion)
                }.toString()
            }

            kgit.branch.isOnMainBranch(context.mainBranch, context.forTesting) -> {
                val stageVersionCalculator = StageVersionCalculator()
                stageVersionCalculator.calculate(latestVersion, context)
            }

            // Works for any branch
            else -> {
                // Compute based on the branch name, otherwise, use the stage to compute the next version
                if (context.stage == Stage.Auto) {
                    val branchVersionCalculator = BranchVersionCalculator(kgit)
                    branchVersionCalculator.calculate(latestNonPreReleaseVersion, context)
                } else {
                    val stageVersionCalculator = StageVersionCalculator()
                    stageVersionCalculator.calculate(latestVersion, context)
                }
            }
        }

        return version
    }
}
