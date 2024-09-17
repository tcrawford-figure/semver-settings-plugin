package io.github.tcrawford.versioning.gradle

import io.github.tcrawford.versioning.kit.render.Element
import io.github.tcrawford.versioning.kit.render.Scribe

fun local(
    fn: BuildCacheLocal.Builder.() -> Unit,
): BuildCacheLocal {
    val builder = BuildCacheLocal.Builder()
    builder.fn()
    return builder.build()
}

class BuildCacheLocal(
    private val directory: BuildCacheDirectory? = null,
) : Element.Block {
    override val name: String = "local"

    override fun render(scribe: Scribe): String = scribe.block(this) { s ->
        directory?.render(s)
    }

    class Builder {
        var directory: BuildCacheDirectory? = null

        fun build(): BuildCacheLocal =
            BuildCacheLocal(
                directory = directory,
            )
    }
}
