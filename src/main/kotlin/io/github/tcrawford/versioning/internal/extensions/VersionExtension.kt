package io.github.tcrawford.versioning.internal.extensions

import io.github.tcrawford.versioning.internal.properties.Modifier
import io.github.tcrawford.versioning.internal.properties.Stage
import io.github.z4kn4fein.semver.Inc
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.inc
import io.github.z4kn4fein.semver.nextPatch
import io.github.z4kn4fein.semver.nextPreRelease

fun Version.nextVersion(providedStage: Stage, providedModifier: Modifier): Version = when {
    isInvalidVersionForComputation() -> {
        error(
            "Cannot compute next version because the latest computed version likely contains branch " +
                "information: $this. If you see this error, please file an issue. This is a bug.",
        )
    }

    // next snapshot
    (providedStage == Stage.Snapshot) -> {
        nextSnapshot(providedModifier.toInc())
    }

    // next pre-release
    (providedModifier == Modifier.Auto && this.isPreRelease) &&
        (providedStage == Stage.Auto || providedStage == this.stage) -> {
        nextPreRelease()
    }

    // next stable
    (providedStage == Stage.Auto && this.isNotPreRelease) || providedStage == Stage.Stable -> {
        nextStable(providedModifier.toInc())
    }

    // next stable with next pre-release identifier
    providedStage == Stage.Auto && this.isPreRelease -> {
        nextStableWithPreRelease(providedModifier.toInc(), this.preRelease)
    }

    // next stable with new pre-release identifier
    providedModifier != Modifier.Auto -> {
        newPreRelease(providedModifier.toInc(), providedStage)
    }

    // next patch with new pre-release identifier
    else -> {
        nextPatch("${providedStage.value}.1")
    }
}

private fun Version.nextStable(incrementer: Inc): Version =
    inc(incrementer)

private fun Version.nextStableWithPreRelease(incrementer: Inc, preRelease: String?): Version =
    inc(incrementer, preRelease)

private fun Version.nextSnapshot(incrementer: Inc): Version =
    inc(incrementer, Stage.Snapshot.value)

private fun Version.newPreRelease(incrementer: Inc, stage: Stage): Version =
    inc(incrementer, "${stage.value}.1")

private val Version.stage: Stage?
    get() = Stage.entries.find { preRelease?.contains(it.value, ignoreCase = true) == true }

/**
 * This is different from being stable or not.
 *
 * "stable" means that the major version is greater than 0 AND does not have a pre-release identifier.
 *
 * This just means that the version lacks a pre-release identifier.
 */
val Version.isNotPreRelease: Boolean
    get() = !isPreRelease

/**
 * Current version is invalid for computation when:
 * - A pre-release
 * - The pre-release label is not a valid stage (aka the version has a branch-based pre-release label)
 */
private fun Version.isInvalidVersionForComputation(): Boolean {
    val stages = Stage.entries.map { it.value.lowercase() }
    val prereleaseLabel = preRelease?.substringBefore(".")?.lowercase()

    return isPreRelease && prereleaseLabel !in stages
}
