package io.github.tcrawford.versioning.gradle

import io.github.tcrawford.versioning.kit.render.Element
import io.github.tcrawford.versioning.kit.render.Scribe
import java.io.File

fun local(
    fn: BuildCacheLocal.Builder.() -> Unit,
): BuildCacheLocal {
    val builder = BuildCacheLocal.Builder()
    builder.fn()
    return builder.build()
}

class BuildCacheLocal(
    private val directory: File? = null,
) : Element.Block {
    override val name: String = "local"

    override fun render(scribe: Scribe): String = scribe.block(this) { s ->
        directory?.let {
            s.line {
                s.append("directory = file(\"")
                s.append(directory.absolutePath)
                s.append("\")")
            }
        }
    }

    class Builder {
        var directory: File? = null

        fun build(): BuildCacheLocal =
            BuildCacheLocal(
                directory = directory,
            )
    }
}
