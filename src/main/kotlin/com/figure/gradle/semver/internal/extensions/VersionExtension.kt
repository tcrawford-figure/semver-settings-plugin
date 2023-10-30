package com.figure.gradle.semver.internal.extensions

import com.figure.gradle.semver.internal.Stage
import io.github.z4kn4fein.semver.Inc
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.inc

internal fun Version.nextStable(incrementer: Inc): Version =
    inc(incrementer)

internal fun Version.nextSnapshot(incrementer: Inc): Version =
    inc(incrementer, Stage.Snapshot.value)

internal fun Version.newPreRelease(incrementer: Inc, stage: Stage): Version =
    inc(incrementer, "${stage.value}.1")

internal fun Version.nextPreRelease(): Version =
    inc(Inc.PRE_RELEASE)
