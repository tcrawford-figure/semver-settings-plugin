package io.github.tcrawford.gradle.semver.internal.properties

internal enum class SemverProperty(val property: String) {
    Stage("semver.stage"),
    Modifier("semver.modifier"),
    TagPrefix("semver.tagPrefix"),
    OverrideVersion("semver.overrideVersion"),

    ForTesting("semver.forTesting")
}
