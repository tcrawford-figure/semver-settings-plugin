package io.github.tcrawford.gradle.semver.internal.calculator

import io.github.tcrawford.gradle.semver.internal.Modifier
import io.github.tcrawford.gradle.semver.internal.Stage
import io.github.tcrawford.gradle.semver.internal.command.KGit
import io.github.z4kn4fein.semver.Version
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
    overrideVersion: Provider<Version>,
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
        val overrideVersion: Property<Version>
        val rootDir: RegularFileProperty
        val mainBranch: Property<String>
        val developmentBranch: Property<String>
    }

    override fun obtain(): String? {
        val rootDir = parameters.rootDir.map { it.asFile }
        val kgit = KGit(directory = rootDir.get())

        val overrideVersion = parameters.overrideVersion.orNull
        val stage = parameters.stage.get()
        val modifier = parameters.modifier.get()
        val forTesting = parameters.forTesting.get()
        val mainBranch = parameters.mainBranch.orNull
        val developmentBranch = parameters.developmentBranch.orNull

        val context = VersionCalculatorContext(
            stage = stage,
            modifier = modifier,
            forTesting = forTesting,
            mainBranch = mainBranch,
            developmentBranch = developmentBranch,
        )

        val version = when {
            overrideVersion != null -> {
                overrideVersion.toString()
            }

            kgit.branch.isOnMainBranch(mainBranch, forTesting) -> {
                val stageBasedVersionCalculator = StageBasedVersionCalculator()
                val latestVersion = kgit.tags.latestOrInitial(parameters.initialVersion)
                stageBasedVersionCalculator.calculate(latestVersion, context)
            }

            // Works for any branch
            else -> {
                // Compute based on the branch name, otherwise, use the stage to compute the next version
                if (stage == Stage.Auto) {
                    val branchBasedVersionCalculator = BranchBasedVersionCalculator(kgit)
                    val latestVersion = kgit.tags.latestNonPreReleaseOrInitial(parameters.initialVersion)
                    branchBasedVersionCalculator.calculate(latestVersion, context)
                } else {
                    val stageBasedVersionCalculator = StageBasedVersionCalculator()
                    val latestVersion = kgit.tags.latestOrInitial(parameters.initialVersion)
                    stageBasedVersionCalculator.calculate(latestVersion, context)
                }
            }
        }

        return version
    }
}
