package io.github.tcrawford.versioning.kit.render

// TODO: Switch to official version if internal is removed from Scribe API
public sealed interface Element {

    public fun render(scribe: Scribe): String

    public fun start(indent: Int): String = " ".repeat(indent)

    public interface Block : Element {
        public val name: String
    }

    public interface Line : Element
}
