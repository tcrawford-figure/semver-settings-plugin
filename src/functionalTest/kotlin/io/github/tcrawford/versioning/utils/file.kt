package io.github.tcrawford.versioning.utils

import java.io.File

fun File.registerForCleanup(): File {
    FileShutdownCache.add(this)
    return this
}
