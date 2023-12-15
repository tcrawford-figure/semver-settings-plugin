package io.github.tcrawford.versioning.internal.extensions

import java.io.File

fun File.getOrCreate(): File {
    if (!exists()) {
        parentFile?.mkdirs()
        createNewFile()
    }
    return this
}
