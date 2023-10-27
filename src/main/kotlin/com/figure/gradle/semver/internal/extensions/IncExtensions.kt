package com.figure.gradle.semver.internal.extensions

import io.github.z4kn4fein.semver.Inc

fun String.toInc(): Inc = Inc.valueOf(this)
