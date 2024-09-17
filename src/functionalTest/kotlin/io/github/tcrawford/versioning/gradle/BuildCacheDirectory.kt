package io.github.tcrawford.versioning.gradle

import io.github.tcrawford.versioning.kit.render.Element
import io.github.tcrawford.versioning.kit.render.Scribe
import java.io.File

fun File.toDirectory(): BuildCacheDirectory = BuildCacheDirectory(this)

fun directory(
    fn: BuildCacheDirectory.Builder.() -> Unit,
): BuildCacheDirectory {
    val builder = BuildCacheDirectory.Builder()
    builder.fn()
    return builder.build()
}

class BuildCacheDirectory(
    val directory: File? = null
) : Element.Line {
    override fun render(scribe: Scribe): String = scribe.line{ s ->
        directory?.let {
            s.append("directory = file(\"")
            s.append(it.absolutePath)
            s.append("\")")
        }
    }

    class Builder {
        var directory: File? = null

        fun build(): BuildCacheDirectory =
            BuildCacheDirectory(
                directory = directory
            )
    }
}
