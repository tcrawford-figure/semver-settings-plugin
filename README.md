# Semver Plugin for Gradle Settings

This Semantic Versioning Gradle settings plugin provides a simple approach to adding semantic versioning to your gradle project using git
history regardless of git strategies.

## Installation

```kotlin
// root project settings.gradle.kts
plugins {
    id("io.github.tcrawford.gradle.semver") version "<current_version>"
}
```

## Configuration

```kotlin
import io.github.tcrawford.gradle.semver.semver

// This is purely for example purposes
semver {
    rootProjectDir.set(settingsDir.parent)  // Default: `settings.settingsDir`
    initialVersion.set("1.0.0")             // Default: `0.0.0` (first build will generate `0.0.1`) 
    mainBranch.set("trunk")                 // No "default", but the plugin will search for `main`, `master` in that order
    developmentBranch.set("development")    // No "default", but the plugin will search for `develop`, `devel`, `dev` in that order
}
```

## Usage

This plugin defines four Gradle parameter properties that the plugin uses to determine the next version based on the latest tag:
`semver.stage`, `semver.modifier`, `semver.overrideVersion`, and `semver.tagPrefix`.

- `semver.stage` - indicates the stage to be used for calculation
- `semver.modifier` - indicates the modifier to be used for calculation
- `semver.overrideVersion` - indicates the exact version to be used for the build. Must adhere to semantic versioning specification.
- `semver.tagPrefix` - indicates the tag prefix to be used for calculation

| Parameter                | Valid Values                                                                                         | Default Value | Optional |
|--------------------------|------------------------------------------------------------------------------------------------------|---------------|----------|
| `semver.stage`           | See [Stage.kt](src/main/kotlin/io/github/tcrawford/gradle/semver/internal/properties/Stage.kt)       | `auto`        | Yes      |
| `semver.modifier`        | See [Modifier.kt](src/main/kotlin/io/github/tcrawford/gradle/semver/internal/properties/Modifier.kt) | `auto`        | Yes      |
| `semver.overrideVersion` | Any valid semantic version                                                                           | N/A           | Yes      |
| `semver.tagPrefix`       | Any valid tag text                                                                                   | `v`           | Yes      |


## Plugin Needs

Support the following scenarios:

- Add suggestions if no git directory can be found
- Build or dirty mode (possibly lower priority):
    - `+build.#` - build mode, for local development. `#` starts at 1 and incremented on each build. Guarantees
      uniqueness without needing to make a commit.
    - `+DIRTY` - dirty mode, where changes have been made, but no commit made yet. Applies to both modes above
- Somehow support tests in GHA that are currently not possible. More documentation can be found in current plugin.
- Support `release/*` branches for stable releases
    - Consider making them smart so that release/v2.x supports the v2 line and release/v3.x supports the v3 line, etc.

## Documentation Needs

- Branching strategies supported and references to how they're canonically defined

## Branching Workflows to Support

### Git Flow

Resources:

- https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow

### GitHub Flow

Resources:

- https://githubflow.github.io/
- https://docs.github.com/en/get-started/quickstart/github-flow

### GitLab Flow

Resources:

- https://about.gitlab.com/topics/version-control/what-is-gitlab-flow/

### Other Workflows to consider later

- OneFlow: https://www.endoflineblog.com/oneflow-a-git-branching-model-and-workflow

## Inspiration

- GitVersion: https://github.com/GitTools/GitVersion
