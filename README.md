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
    rootProjectDir.set(settingsDir.parent)

    // Default: `0.0.0` (first build will generate `0.0.1`)
    initialVersion.set("1.0.0")

    // No "default", but the plugin will search for `main`, `master` in that order
    mainBranch.set("trunk")

    // No "default", but the plugin will search for:
    // `develop`, `devel`, `dev` in that order
    developmentBranch.set("development")
}
```

## Documentation

### [tcrawford-figure.github.io/git-aware-versioning-plugin](https://tcrawford-figure.github.io/git-aware-versioning-plugin)

## Plugin Needs

Support the following scenarios:

- Build or dirty mode (possibly lower priority):
    - `+build.#` - build mode, for local development. `#` starts at 1 and
      incremented on each build. Guarantees
      uniqueness without needing to make a commit.
    - `+DIRTY` - dirty mode, where changes have been made, but no commit made
      yet. Applies to both modes above
- Support `release/*` branches for stable releases
    - Consider making them smart so that release/v2.x supports the v2 line and
      release/v3.x supports the v3 line, etc.

## Documentation Needs

- Branching strategies supported and references to how they're canonically
  defined

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

-
OneFlow: https://www.endoflineblog.com/oneflow-a-git-branching-model-and-workflow

## Inspiration

- GitVersion: https://github.com/GitTools/GitVersion
