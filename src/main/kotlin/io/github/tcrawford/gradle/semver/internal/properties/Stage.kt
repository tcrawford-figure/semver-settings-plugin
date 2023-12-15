package io.github.tcrawford.gradle.semver.internal.properties

// In order from lowest to highest priority
enum class Stage(val value: String) {
    Dev("dev"),
    Alpha("alpha"),
    Beta("beta"),
    RC("rc"),
    Snapshot("SNAPSHOT"),
    Final("final"),
    GA("ga"),
    Release("release"),
    Stable("stable"),
    Auto("auto")
    ;

    companion object {
        fun fromValue(value: String): Stage =
            entries.find { it.value.lowercase() == value.lowercase() }
                ?: error("Invalid stage provided: $value. Valid values are: ${entries.joinToString { it.value.lowercase() }}")
    }
}
