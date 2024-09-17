package io.github.tcrawford.versioning.gradle

import io.github.tcrawford.versioning.kit.render.Element
import io.github.tcrawford.versioning.kit.render.Scribe

fun buildCache(
    fn: BuildCache.Builder.() -> Unit,
): BuildCache {
    val builder = BuildCache.Builder()
    builder.fn()
    return builder.build()
}

class BuildCache(
    private val local: BuildCacheLocal? = null,
) : Element.Block {
    override val name: String = "buildCache"

    override fun render(scribe: Scribe): String = scribe.block(this) { s ->
        local?.render(s)
    }

    class Builder {
        var local: BuildCacheLocal? = null

        fun build(): BuildCache =
            BuildCache(
                local = local,
            )
    }
}
