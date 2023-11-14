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

@Suppress("LongParameterList")
internal fun ProviderFactory.versionFactory(
    initialVersion: Property<String>,
    stage: Provider<Stage>,
    modifier: Provider<Modifier>,
    forTesting: Provider<Boolean>,
    overrideVersion: Provider<Version>,
    rootDir: RegularFileProperty
): Provider<String> =
    of(VersionFactory::class.java) { spec ->
        spec.parameters {
            it.initialVersion.set(initialVersion)
            it.stage.set(stage)
            it.modifier.set(modifier)
            it.forTesting.set(forTesting)
            it.overrideVersion.set(overrideVersion)
            it.rootDir.set(rootDir)
        }
    }

internal abstract class VersionFactory : ValueSource<String, VersionFactory.Params> {
    interface Params : ValueSourceParameters {
        val initialVersion: Property<String>
        val stage: Property<Stage>
        val modifier: Property<Modifier>
        val forTesting: Property<Boolean>
        val overrideVersion: Property<Version>
        val rootDir: RegularFileProperty
    }

    override fun obtain(): String? {
        val rootDir = parameters.rootDir.map { it.asFile }
        val kgit = KGit(directory = rootDir.get())

        val latestVersion = kgit.tags.latestOrInitial(parameters.initialVersion)
        val overrideVersion = parameters.overrideVersion.orNull
        val stage = parameters.stage.get()
        val modifier = parameters.modifier.get()
        val forTesting = parameters.forTesting.get()

        val version = when {
            overrideVersion != null -> {
                overrideVersion.toString()
            }

            kgit.branch.isOnMainBranch(parameters.forTesting.get()) -> {
                val stageBasedVersionCalculator = StageBasedVersionCalculator()
                stageBasedVersionCalculator.calculate(latestVersion, stage, modifier, forTesting)
            }

            // Works for any branch
            else -> {
                // Compute based on the branch name, otherwise, use the stage to compute the next version
                if (stage == Stage.Auto) {
                    val branchBasedVersionCalculator = BranchBasedVersionCalculator(kgit)
                    branchBasedVersionCalculator.calculate(latestVersion, stage, modifier, forTesting)
                } else {
                    val stageBasedVersionCalculator = StageBasedVersionCalculator()
                    stageBasedVersionCalculator.calculate(latestVersion, stage, modifier, forTesting)
                }
            }
        }

        return version
    }
}
