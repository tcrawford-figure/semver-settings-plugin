package io.github.tcrawford.versioning.internal.properties

enum class SemverProperty(val property: String) {
    Stage("semver.stage"),
    Modifier("semver.modifier"),
    TagPrefix("semver.tagPrefix"),
    OverrideVersion("semver.overrideVersion"),
    ForMajorVersion("semver.forMajorVersion"),

    ForTesting("semver.forTesting"),
}
