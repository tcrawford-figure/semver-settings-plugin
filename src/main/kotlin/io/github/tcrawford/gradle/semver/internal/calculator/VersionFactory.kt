package io.github.tcrawford.gradle.semver.internal.calculator

import io.github.tcrawford.gradle.semver.errors.InvalidOverrideVersionError
import io.github.tcrawford.gradle.semver.internal.Modifier
import io.github.tcrawford.gradle.semver.internal.Stage
import io.github.tcrawford.gradle.semver.internal.command.GitState
import io.github.tcrawford.gradle.semver.internal.command.KGit
import io.github.z4kn4fein.semver.toVersion
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters

// TODO: See if this list can be a data class we own
@Suppress("LongParameterList")
internal fun ProviderFactory.versionFactory(
    initialVersion: Property<String>,
    stage: Provider<Stage>,
    modifier: Provider<Modifier>,
    forTesting: Provider<Boolean>,
    overrideVersion: Provider<String>,
    rootDir: RegularFileProperty,
    mainBranch: Provider<String>,
    developmentBranch: Provider<String>,
): Provider<String> =
    of(VersionFactory::class.java) { spec ->
        spec.parameters {
            it.initialVersion.set(initialVersion)
            it.stage.set(stage)
            it.modifier.set(modifier)
            it.forTesting.set(forTesting)
            it.overrideVersion.set(overrideVersion)
            it.rootDir.set(rootDir)
            it.mainBranch.set(mainBranch)
            it.developmentBranch.set(developmentBranch)
        }
    }

internal abstract class VersionFactory : ValueSource<String, VersionFactory.Params> {
    // TODO: See if this can be the data class from above, as a provider
    interface Params : ValueSourceParameters {
        val initialVersion: Property<String>
        val stage: Property<Stage>
        val modifier: Property<Modifier>
        val forTesting: Property<Boolean>
        val overrideVersion: Property<String>
        val rootDir: RegularFileProperty
        val mainBranch: Property<String>
        val developmentBranch: Property<String>
    }

    private fun Params.toVersionCalculatorContext(gitState: GitState) =
        VersionCalculatorContext(
            stage = stage.get(),
            modifier = modifier.get(),
            forTesting = forTesting.get(),
            gitState = gitState,
            mainBranch = mainBranch.orNull,
            developmentBranch = developmentBranch.orNull,
        )

    override fun obtain(): String {
        val rootDir = parameters.rootDir.map { it.asFile }
        val kgit = KGit(directory = rootDir.get())

        val context = parameters.toVersionCalculatorContext(kgit.state())

        val overrideVersion = parameters.overrideVersion.orNull
        val latestVersion = kgit.tags.latestOrInitial(parameters.initialVersion)
        val latestNonPreReleaseVersion = kgit.tags.latestNonPreReleaseOrInitial(parameters.initialVersion)

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
