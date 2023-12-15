package io.github.tcrawford.gradle.semver.internal.properties

enum class SemverProperty(val property: String) {
    Stage("semver.stage"),
    Modifier("semver.modifier"),
    TagPrefix("semver.tagPrefix"),
    OverrideVersion("semver.overrideVersion"),

    ForTesting("semver.forTesting")
}
