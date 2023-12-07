# Semver Plugin for Gradle Settings

This Semantic Versioning Gradle settings plugin provides a simple approach to
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
    id("io.github.tcrawford.gradle.semver") version "<current_version>"
}
```

## Configuration

```kotlin
import io.github.tcrawford.gradle.semver.semver

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

## Usage

This plugin defines four Gradle parameter properties that the plugin uses to
determine the next version based on the latest tag: `semver.stage`,
 `semver.modifier`, `semver.overrideVersion`, and `semver.tagPrefix`.

- `semver.stage` - indicates the stage to be used for calculation
- `semver.modifier` - indicates the modifier to be used for calculation
- `semver.overrideVersion` - indicates the exact version to be used for the
  build. Must adhere to semantic versioning specification.
- `semver.tagPrefix` - indicates the tag prefix to be used for calculation

| Parameter                | Valid Values                                                                                                            | Default Value | Optional |
|--------------------------|-------------------------------------------------------------------------------------------------------------------------|---------------|----------|
| `semver.stage`           | See [Stage.kt](src/main/kotlin/io/github/tcrawford/gradle/semver/internal/properties/Stage.kt) (case insensitive)       | `auto`        | Yes      |
| `semver.modifier`        | See [Modifier.kt](src/main/kotlin/io/github/tcrawford/gradle/semver/internal/properties/Modifier.kt) (case insensitive) | `auto`        | Yes      |
| `semver.overrideVersion` | Any valid semantic version                                                                                              | `null`        | Yes      |
| `semver.tagPrefix`       | Any valid tag text                                                                                                      | `v`           | Yes      |

The following are simple examples of building with plugin parameters:

```shell
# Most minimal usage is to provide no parameters
./gradlew build
# This is functionally equivalent to
./gradlew build -Psemver.stage=auto -Psemver.modifier=auto -Psemver.tagPrefix=v

# Using stage and modifier
./gradlew build -Psemver.stage=rc -Psemver.modifier=minor

# Using override version
./gradlew build -Psemver.overrideVersion=9.9.9

# Using tag prefix
./gradlew build -Psemver.tagPrefix=r
```

## Stages

To alter the next version stage, use the Gradle
property: `-Psemver.stage=<stage>`.

> [!NOTE]
> If no stage is provided, it will default to `auto`.

The following are possible values:

| Stage      | Pre-release Label         | Example Tag        | Description                |
|------------|---------------------------|--------------------|----------------------------|
| `dev`      | dev                       | `v1.0.0-dev.1`     | Development stage          |
| `alpha`    | alpha                     | `v1.0.0-alpha.1`   | Alpha stage                |
| `beta`     | beta                      | `v1.0.0-beta.1`    | Beta stage                 |
| `rc`       | rc                        | `v1.0.0-rc.1`      | Release Candidate stage    |
| `snapshot` | SNAPSHOT                  | `v1.0.0-SNAPSHOT`  | Snapshot stage             |
| `final`    | final                     | `v1.0.0-final.1`   | Final stage                |
| `ga`       | ga                        | `v1.0.0-ga.1`      | General Availability stage |
| `release`  | release                   | `v1.0.0-release.1` | Release stage              |
| `stable`   | (none)                    | `v1.0.0`           | Stable stage               |
| `auto`     | (depends on previous tag) | -                  | Based on previous tag      |

### Examples

> [!TIP]
> Since no modifier is provided in these examples, the default modifier
> of `auto` used.

Latest tags: `v1.0.0-rc.1`

| Command                             | Next Version    |
|-------------------------------------|-----------------|
| `./gradlew -Psemver.stage=dev`      | 1.0.1-dev.1     |
| `./gradlew -Psemver.stage=alpha`    | 1.0.1-alpha.1   |
| `./gradlew -Psemver.stage=beta`     | 1.0.1-beta.1    |
| `./gradlew -Psemver.stage=rc`       | 1.0.0-rc.2      |
| `./gradlew -Psemver.stage=snapshot` | 1.0.1-SNAPSHOT  |
| `./gradlew -Psemver.stage=final`    | 1.0.1-final.1   |
| `./gradlew -Psemver.stage=ga`       | 1.0.1-ga.1      |
| `./gradlew -Psemver.stage=release`  | 1.0.1-release.1 |
| `./gradlew -Psemver.stage=stable`   | 1.0.0           |
| `./gradlew -Psemver.stage=auto`     | 1.0.0-rc.2      |

Latest tag: `v1.0.0`

| Command                             | Next Version    |
|-------------------------------------|-----------------|
| `./gradlew -Psemver.stage=dev`      | 1.0.1-dev.1     |
| `./gradlew -Psemver.stage=alpha`    | 1.0.1-alpha.1   |
| `./gradlew -Psemver.stage=beta`     | 1.0.1-beta.1    |
| `./gradlew -Psemver.stage=rc`       | 1.0.1-rc.1      |
| `./gradlew -Psemver.stage=snapshot` | 1.0.1-SNAPSHOT  |
| `./gradlew -Psemver.stage=final`    | 1.0.1-final.1   |
| `./gradlew -Psemver.stage=ga`       | 1.0.1-ga.1      |
| `./gradlew -Psemver.stage=release`  | 1.0.1-release.1 |
| `./gradlew -Psemver.stage=stable`   | 1.0.1           |
| `./gradlew -Psemver.stage=auto`     | 1.0.1           |

## Modifiers

To alter the next version incrementation, use the Gradle
property: `-Psemver.modifier=<modifier>`.

> [!NOTE]
> If no modifier is provided, it will default to `auto`.

The following are the possible values:

| Modifier | Description                                                                                      |
|----------|--------------------------------------------------------------------------------------------------|
| `major`  | Increments the major version number                                                              |
| `minor`  | Increments the minor version number                                                              |
| `patch`  | Increments the patch version number                                                              |
| `auto`   | Increments the patch or the pre-release number if the previous tag was a stage-based pre-release |

### Examples

> [!TIP]
> Since no stage is provided in these examples, the default stage of `auto`
> used.

Latest tag: `v1.0.0-rc.1`

| Command                             | Next Version |
|-------------------------------------|--------------|
| `./gradlew -Psemver.modifier=major` | v2.0.0       |
| `./gradlew -Psemver.modifier=minor` | v1.1.0       |
| `./gradlew -Psemver.modifier=patch` | v1.0.1       |
| `./gradlew -Psemver.modifier=auto`  | v1.0.0-rc.2  |

Latest tag: `v1.0.0`

| Command                             | Next Version |
|-------------------------------------|--------------|
| `./gradlew -Psemver.modifier=major` | v2.0.0       |
| `./gradlew -Psemver.modifier=minor` | v1.1.0       |
| `./gradlew -Psemver.modifier=patch` | v1.0.1       |
| `./gradlew -Psemver.modifier=auto`  | v1.0.1       |

Latest tags (sorted by latest first):

- `v1.0.1-my-feature.1`
- `v1.0.0`

> [!IMPORTANT]
> The latest tag is `v1.0.1-my-feature.1`, however, this is a special
> pre-release type that does not affect the calculation of the
> next version given a modifier.

| Command                             | Next Version |
|-------------------------------------|--------------|
| `./gradlew -Psemver.modifier=major` | v2.0.0       |
| `./gradlew -Psemver.modifier=minor` | v1.1.0       |
| `./gradlew -Psemver.modifier=patch` | v1.0.1       |
| `./gradlew -Psemver.modifier=auto`  | v1.0.1       |

## Stage & Modifier Combination

Latest tag: `v1.0.0-rc.1`

| Command                                                     | Next Version    |
|-------------------------------------------------------------|-----------------|
| `./gradlew -Psemver.stage=dev      -Psemver.modifier=major` | 2.0.0-dev.1     |
| `./gradlew -Psemver.stage=alpha    -Psemver.modifier=major` | 2.0.0-alpha.1   |
| `./gradlew -Psemver.stage=beta     -Psemver.modifier=major` | 2.0.0-beta.1    |
| `./gradlew -Psemver.stage=rc       -Psemver.modifier=major` | 2.0.0-rc.1      |
| `./gradlew -Psemver.stage=snapshot -Psemver.modifier=major` | 2.0.0-SNAPSHOT  |
| `./gradlew -Psemver.stage=final    -Psemver.modifier=major` | 2.0.0-final.1   |
| `./gradlew -Psemver.stage=ga       -Psemver.modifier=major` | 2.0.0-ga.1      |
| `./gradlew -Psemver.stage=release  -Psemver.modifier=major` | 2.0.0-release.1 |
| `./gradlew -Psemver.stage=stable   -Psemver.modifier=major` | 2.0.0           |
| `./gradlew -Psemver.stage=auto     -Psemver.modifier=major` | 2.0.0-rc.1      |

Latest tag: `v1.0.0-rc.1`

| Command                                                     | Next Version    |
|-------------------------------------------------------------|-----------------|
| `./gradlew -Psemver.stage=dev      -Psemver.modifier=minor` | 1.1.0-dev.1     |
| `./gradlew -Psemver.stage=alpha    -Psemver.modifier=minor` | 1.1.0-alpha.1   |
| `./gradlew -Psemver.stage=beta     -Psemver.modifier=minor` | 1.1.0-beta.1    |
| `./gradlew -Psemver.stage=rc       -Psemver.modifier=minor` | 1.1.0-rc.1      |
| `./gradlew -Psemver.stage=snapshot -Psemver.modifier=minor` | 1.1.0-SNAPSHOT  |
| `./gradlew -Psemver.stage=final    -Psemver.modifier=minor` | 1.1.0-final.1   |
| `./gradlew -Psemver.stage=ga       -Psemver.modifier=minor` | 1.1.0-ga.1      |
| `./gradlew -Psemver.stage=release  -Psemver.modifier=minor` | 1.1.0-release.1 |
| `./gradlew -Psemver.stage=stable   -Psemver.modifier=minor` | 1.1.0           |
| `./gradlew -Psemver.stage=auto     -Psemver.modifier=minor` | 1.1.0-rc.1      |

Latest tag: `v1.0.0-rc.1`

| Command                                                     | Next Version    |
|-------------------------------------------------------------|-----------------|
| `./gradlew -Psemver.stage=dev      -Psemver.modifier=patch` | 1.0.1-dev.1     |
| `./gradlew -Psemver.stage=alpha    -Psemver.modifier=patch` | 1.0.1-alpha.1   |
| `./gradlew -Psemver.stage=beta     -Psemver.modifier=patch` | 1.0.1-beta.1    |
| `./gradlew -Psemver.stage=rc       -Psemver.modifier=patch` | 1.0.1-rc.1      |
| `./gradlew -Psemver.stage=snapshot -Psemver.modifier=patch` | 1.0.1-SNAPSHOT  |
| `./gradlew -Psemver.stage=final    -Psemver.modifier=patch` | 1.0.1-final.1   |
| `./gradlew -Psemver.stage=ga       -Psemver.modifier=patch` | 1.0.1-ga.1      |
| `./gradlew -Psemver.stage=release  -Psemver.modifier=patch` | 1.0.1-release.1 |
| `./gradlew -Psemver.stage=stable   -Psemver.modifier=patch` | 1.0.1           |
| `./gradlew -Psemver.stage=auto     -Psemver.modifier=patch` | 1.0.1-rc.1      |

Latest tag: `v1.0.0-rc.1`

| Command                                                    | Next Version    |
|------------------------------------------------------------|-----------------|
| `./gradlew -Psemver.stage=dev      -Psemver.modifier=auto` | 1.0.1-dev.1     |
| `./gradlew -Psemver.stage=alpha    -Psemver.modifier=auto` | 1.0.1-alpha.1   |
| `./gradlew -Psemver.stage=beta     -Psemver.modifier=auto` | 1.0.1-beta.1    |
| `./gradlew -Psemver.stage=rc       -Psemver.modifier=auto` | 1.0.0-rc.2      |
| `./gradlew -Psemver.stage=snapshot -Psemver.modifier=auto` | 1.0.1-SNAPSHOT  |
| `./gradlew -Psemver.stage=final    -Psemver.modifier=auto` | 1.0.1-final.1   |
| `./gradlew -Psemver.stage=ga       -Psemver.modifier=auto` | 1.0.1-ga.1      |
| `./gradlew -Psemver.stage=release  -Psemver.modifier=auto` | 1.0.1-release.1 |
| `./gradlew -Psemver.stage=stable   -Psemver.modifier=auto` | 1.0.1           |
| `./gradlew -Psemver.stage=auto     -Psemver.modifier=auto` | 1.0.0-rc.2      |

Latest tag: `v1.0.0`

| Command                                                     | Next Version    |
|-------------------------------------------------------------|-----------------|
| `./gradlew -Psemver.stage=dev      -Psemver.modifier=major` | 2.0.0-dev.1     |
| `./gradlew -Psemver.stage=alpha    -Psemver.modifier=major` | 2.0.0-alpha.1   |
| `./gradlew -Psemver.stage=beta     -Psemver.modifier=major` | 2.0.0-beta.1    |
| `./gradlew -Psemver.stage=rc       -Psemver.modifier=major` | 2.0.0-rc.1      |
| `./gradlew -Psemver.stage=snapshot -Psemver.modifier=major` | 2.0.0-SNAPSHOT  |
| `./gradlew -Psemver.stage=final    -Psemver.modifier=major` | 2.0.0-final.1   |
| `./gradlew -Psemver.stage=ga       -Psemver.modifier=major` | 2.0.0-ga.1      |
| `./gradlew -Psemver.stage=release  -Psemver.modifier=major` | 2.0.0-release.1 |
| `./gradlew -Psemver.stage=stable   -Psemver.modifier=major` | 2.0.0           |
| `./gradlew -Psemver.stage=auto     -Psemver.modifier=major` | 2.0.0           |

Latest tag: `v1.0.0`

| Command                                                     | Next Version    |
|-------------------------------------------------------------|-----------------|
| `./gradlew -Psemver.stage=dev      -Psemver.modifier=minor` | 1.1.0-dev.1     |
| `./gradlew -Psemver.stage=alpha    -Psemver.modifier=minor` | 1.1.0-alpha.1   |
| `./gradlew -Psemver.stage=beta     -Psemver.modifier=minor` | 1.1.0-beta.1    |
| `./gradlew -Psemver.stage=rc       -Psemver.modifier=minor` | 1.1.0-rc.1      |
| `./gradlew -Psemver.stage=snapshot -Psemver.modifier=minor` | 1.1.0-SNAPSHOT  |
| `./gradlew -Psemver.stage=final    -Psemver.modifier=minor` | 1.1.0-final.1   |
| `./gradlew -Psemver.stage=ga       -Psemver.modifier=minor` | 1.1.0-ga.1      |
| `./gradlew -Psemver.stage=release  -Psemver.modifier=minor` | 1.1.0-release.1 |
| `./gradlew -Psemver.stage=stable   -Psemver.modifier=minor` | 1.1.0           |
| `./gradlew -Psemver.stage=auto     -Psemver.modifier=minor` | 1.1.0           |

Latest tag: `v1.0.0`

| Command                                                     | Next Version    |
|-------------------------------------------------------------|-----------------|
| `./gradlew -Psemver.stage=dev      -Psemver.modifier=patch` | 1.0.1-dev.1     |
| `./gradlew -Psemver.stage=alpha    -Psemver.modifier=patch` | 1.0.1-alpha.1   |
| `./gradlew -Psemver.stage=beta     -Psemver.modifier=patch` | 1.0.1-beta.1    |
| `./gradlew -Psemver.stage=rc       -Psemver.modifier=patch` | 1.0.1-rc.1      |
| `./gradlew -Psemver.stage=snapshot -Psemver.modifier=patch` | 1.0.1-SNAPSHOT  |
| `./gradlew -Psemver.stage=final    -Psemver.modifier=patch` | 1.0.1-final.1   |
| `./gradlew -Psemver.stage=ga       -Psemver.modifier=patch` | 1.0.1-ga.1      |
| `./gradlew -Psemver.stage=release  -Psemver.modifier=patch` | 1.0.1-release.1 |
| `./gradlew -Psemver.stage=stable   -Psemver.modifier=patch` | 1.0.1           |
| `./gradlew -Psemver.stage=auto     -Psemver.modifier=patch` | 1.0.1           |

Latest tag: `v1.0.0`

| Command                                                    | Next Version    |
|------------------------------------------------------------|-----------------|
| `./gradlew -Psemver.stage=dev      -Psemver.modifier=auto` | 1.0.1-dev.1     |
| `./gradlew -Psemver.stage=alpha    -Psemver.modifier=auto` | 1.0.1-alpha.1   |
| `./gradlew -Psemver.stage=beta     -Psemver.modifier=auto` | 1.0.1-beta.1    |
| `./gradlew -Psemver.stage=rc       -Psemver.modifier=auto` | 1.0.0-rc.2      |
| `./gradlew -Psemver.stage=snapshot -Psemver.modifier=auto` | 1.0.1-SNAPSHOT  |
| `./gradlew -Psemver.stage=final    -Psemver.modifier=auto` | 1.0.1-final.1   |
| `./gradlew -Psemver.stage=ga       -Psemver.modifier=auto` | 1.0.1-ga.1      |
| `./gradlew -Psemver.stage=release  -Psemver.modifier=auto` | 1.0.1-release.1 |
| `./gradlew -Psemver.stage=stable   -Psemver.modifier=auto` | 1.0.1           |
| `./gradlew -Psemver.stage=auto     -Psemver.modifier=auto` | 1.0.1           |

## Branch-based Version Calculation

Branch-based version calculation provides a way to automatically generate unique versions based on the current branch.
This can be good for local testing and integration testing with other libraries
or services for a period of time prior to creating a stable release.

> [!IMPORTANT]
> Every commit gets a new version!

### Examples

Branching from the main branch

| Latest Tag | Current Branch         | Branched From | Commits past main | Next version                 |
|------------|------------------------|---------------|-------------------|------------------------------|
| `v1.0.0`   | `me/sc-123/my-feature` | `main`        | 4                 | 1.0.1-me-sc-123-my-feature.4 |
| `v1.0.0`   | `my-feature`           | `main`        | 12                | 1.0.1-my-feature.12          |
| `v1.0.0`   | `my-sub-feature`       | `my-feature`  | 16                | 1.0.1-my-sub-feature.16      |
| `v1.0.0`   | `main`                 | -             | 7                 | 1.0.1                        |

Branching from the development branch

| Latest Tag         | Current Branch         | Branched From | Commits past develop | Next version                 |
|--------------------|------------------------|---------------|----------------------|------------------------------|
| `v1.0.0`           | `me/sc-123/my-feature` | `develop`     | 4                    | 1.0.1-me-sc-123-my-feature.4 |
| `v1.0.0`           | `my-feature`           | `develop`     | 12                   | 1.0.1-my-feature.12          |
| `v1.0.0`           | `my-sub-feature`       | `my-feature`  | 16                   | 1.0.1-my-sub-feature.16      |
| `v1.0.0-develop.1` | `develop`              | -             | 7                    | 1.0.1-develop.8              |

### Forcing a new version

> [!TIP]
> Need a new version but don't need to make any changes to your branch?
> Just create an empty commit!

```shell
git commit --allow-empty -m "Empty commit"
```

## Plugin Needs

Support the following scenarios:

- Add suggestions if no git directory can be found
- Build or dirty mode (possibly lower priority):
    - `+build.#` - build mode, for local development. `#` starts at 1 and
      incremented on each build. Guarantees
      uniqueness without needing to make a commit.
    - `+DIRTY` - dirty mode, where changes have been made, but no commit made
      yet. Applies to both modes above
- Somehow support tests in GHA that are currently not possible. More
  documentation can be found in current plugin.
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
