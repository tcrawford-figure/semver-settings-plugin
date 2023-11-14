package com.figure.gradle.semver.internal.extensions

import com.figure.gradle.semver.internal.Modifier
import com.figure.gradle.semver.internal.Stage
import io.github.z4kn4fein.semver.Inc
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.inc
import io.github.z4kn4fein.semver.nextPatch
import io.github.z4kn4fein.semver.nextPreRelease

internal fun Version.nextVersion(providedStage: Stage, providedModifier: Modifier): Version = when {
    (providedStage == Stage.Snapshot) -> {
        nextSnapshot(providedModifier.toInc())
    }

    // next pre-release
    (providedModifier == Modifier.Auto && this.isPreRelease) &&
        (providedStage == Stage.Auto || providedStage == this.stage) -> {
        nextPreRelease()
    }

    // next stable
    (providedStage == Stage.Auto && this.isStable) || providedStage == Stage.Stable -> {
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
