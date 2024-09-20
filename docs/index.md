# Git Aware Versioning Gradle Plugin

This Git Aware Versioning Gradle plugin provides a simple approach to
adding semantic versioning to your gradle project using git
history regardless of git strategies.

At a glance, this plugin provides the following features:

- Support for stages (`rc`, `beta`, `stable`, `snapshot`, etc. - see below)
- Support for modifiers (`auto`, `patch`, `minor`, `major`)
- Support for branch-based version calculations
- Support for overriding the version
- Support for setting an alternate initial version
- Support for specifying alternate main and development branch names
- Support for appending build metadata (format: `+<yyyyMMddHHmm>`)
- Build support when
    - No git repository is present
    - No git tags are present
    - No remote branch is present
    - Merging, rebasing, cherry-picking, bisecting, reverting, or in a detached
      head state

## Installation

The following can be added to any of the following:

- `settings.gradle.kts` (recommended)
- `build.gradle.kts` (root project)
- `build.gradle.kts` (subproject)

If the semantic version is targeting the entire project, it's recommended to add
this to the `settings.gradle.kts` file.

```kotlin
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

    // No "default", but the plugin will search in order for:
    // `main`, `master
    mainBranch = "trunk"

    // No "default", but the plugin will search in order for:
    // `develop`, `devel`, `dev` 
    developmentBranch = "development"

    // Default: `never`
    // Options: `never`, `always`, `locally`
    appendBuildMetadata = "locally"
}
```
