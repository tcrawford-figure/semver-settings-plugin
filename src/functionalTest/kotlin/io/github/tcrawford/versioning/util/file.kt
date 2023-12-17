package io.github.tcrawford.versioning.util

import java.io.File

private fun String.toFile() = File(this)

fun resolveResource(resourcePath: String): File =
    Thread.currentThread().contextClassLoader.getResource(resourcePath)?.file?.toFile()
        ?: throw IllegalArgumentException("Resource not found: $resourcePath")
