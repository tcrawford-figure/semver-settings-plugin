# Git Aware Versioning Gradle Plugin

This Git Aware Versioning Gradle plugin provides a simple approach to
adding semantic versioning to your gradle project using git
history regardless of git strategies.

At a glance, this plugin provides the following features:

- Support for stages (`rc`, `beta`, `stable`, `snapshot`, etc. - see below)
- Support for modifiers (`auto`, `patch`, `minor`, `major`)
- Support for branch-based version calculations
- Support for overriding the version

## Installation

```kotlin
// root project settings.gradle.kts
plugins {
    id("io.github.tcrawford.versioning") version "<current_version>"
}
```

## Configuration

```kotlin
// For older versions of gradle, you may need to import the configuration method
import io.github.tcrawford.versioning.semver

// This is purely for example purposes
semver {
    // Default: `settings.settingsDir`
    rootProjectDir = settingsDir.parent

    // Default: `0.0.0` (first build will generate `0.0.1`)
    initialVersion = "1.0.0"

    // No "default", but the plugin will search for `main`, `master` in that order
    mainBranch = "trunk"

    // No "default", but the plugin will search for:
    // `develop`, `devel`, `dev` in that order
    developmentBranch = "development"
   
    appendBuildMetadata = true
}
```

## Documentation

For more detailed documentation, please visit [tcrawford-figure.github.io/git-aware-versioning-plugin](https://tcrawford-figure.github.io/git-aware-versioning-plugin).

## Plugin Needs

Support the following scenarios:

- `+build.#` - build mode, for local development. `#` is the current date and time.
