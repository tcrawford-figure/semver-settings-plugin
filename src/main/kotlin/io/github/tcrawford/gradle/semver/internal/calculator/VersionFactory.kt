package io.github.tcrawford.gradle.semver.internal.calculator

import io.github.tcrawford.gradle.semver.errors.InvalidOverrideVersionError
import io.github.tcrawford.gradle.semver.internal.Stage
import io.github.tcrawford.gradle.semver.internal.command.GitState
import io.github.tcrawford.gradle.semver.internal.command.KGit
import io.github.z4kn4fein.semver.toVersion
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters

internal fun ProviderFactory.versionFactory(
    context: VersionFactoryContext
): Provider<String> =
    of(VersionFactory::class.java) { spec ->
        spec.parameters {
            it.versionFactoryContext.set(context)
        }
    }

internal abstract class VersionFactory : ValueSource<String, VersionFactory.Params> {
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
                developmentBranch = developmentBranch
            )
        }

    override fun obtain(): String {
        val factoryContext = parameters.versionFactoryContext.get()
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
