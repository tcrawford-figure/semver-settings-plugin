package com.figure.gradle.semver.internal.extensions

import com.figure.gradle.semver.internal.Modifier
import com.figure.gradle.semver.internal.Stage
import io.github.z4kn4fein.semver.Inc
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.inc
import io.github.z4kn4fein.semver.nextPatch
import io.github.z4kn4fein.semver.nextPreRelease

internal fun Version.nextStable(incrementer: Inc): Version =
    inc(incrementer)

internal fun Version.nextSnapshot(incrementer: Inc): Version =
    inc(incrementer, Stage.Snapshot.value)

internal fun Version.newPreRelease(incrementer: Inc, stage: Stage): Version =
    inc(incrementer, "${stage.value}.1")

internal val Version.stage: Stage?
    get() = Stage.values().find { preRelease?.contains(it.value, ignoreCase = true) == true }

fun Version.nextVersion(providedStage: Stage, providedModifier: Modifier): Version = when {
    // next pre-release
    (providedModifier == Modifier.Auto && this.isPreRelease) &&
        (providedStage == Stage.Auto || providedStage == this.stage) -> {
        nextPreRelease()
    }

    // next stable
    (providedStage == Stage.Auto && this.isStable) || providedStage == Stage.Stable -> {
        inc(providedModifier.toInc())
    }

    // next stable with next pre-release identifier
    providedStage == Stage.Auto && this.isPreRelease -> {
        inc(providedModifier.toInc(), this.preRelease)
    }

    // next stable with new pre-release identifier
    providedModifier != Modifier.Auto -> {
        inc(providedModifier.toInc(), "${providedStage.value}.1")
    }

    // next patch with new pre-release identifier
    else -> {
        nextPatch("${providedStage.value}.1")
    }
}
