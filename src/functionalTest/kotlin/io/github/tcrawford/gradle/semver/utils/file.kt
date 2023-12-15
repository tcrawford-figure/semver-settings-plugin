package io.github.tcrawford.gradle.semver.utils

import java.io.File

fun File.registerForCleanup(): File {
    ProjectConfig.FileShutdownCache.add(this)
    return this
}
