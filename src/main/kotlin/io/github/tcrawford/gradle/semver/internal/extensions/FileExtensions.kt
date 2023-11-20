package io.github.tcrawford.gradle.semver.internal.extensions

import java.io.File

internal fun File.getOrCreate(): File {
    if (!exists()) {
        parentFile?.mkdirs()
        createNewFile()
    }
    return this
}

internal fun File.isEmpty(): Boolean =
    length() == 0L
