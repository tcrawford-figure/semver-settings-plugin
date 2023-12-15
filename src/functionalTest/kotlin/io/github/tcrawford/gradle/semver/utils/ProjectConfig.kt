package io.github.tcrawford.gradle.semver.utils

import io.kotest.core.config.AbstractProjectConfig
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue

class ProjectConfig : AbstractProjectConfig() {
    object FileShutdownCache {
        private val cache = ConcurrentLinkedQueue<File>()

        fun add(item: File) {
            cache.add(item)
        }

        fun clean() {
            cache.forEach { it.deleteRecursively() }
        }
    }

    override suspend fun afterProject() {
        FileShutdownCache.clean()
    }
}
