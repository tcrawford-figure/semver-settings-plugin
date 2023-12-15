package io.github.tcrawford.versioning.utils

import io.kotest.core.config.AbstractProjectConfig
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue

object FileShutdownCache {
    private val cache = ConcurrentLinkedQueue<File>()

    fun add(item: File) {
        cache.add(item)
    }

    fun clean() {
        cache.forEach {
            println("Cleaning ${it.absolutePath}")
            it.deleteRecursively()
        }
    }
}

class ProjectConfig : AbstractProjectConfig() {
    override suspend fun afterProject() {
        FileShutdownCache.clean()
    }
}
