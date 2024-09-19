This plugin defines five Gradle parameter properties that the plugin uses to
determine the next version based on the latest tag: `semver.stage`,
`semver.modifier`, `semver.overrideVersion`, `semver.tagPrefix`, and
`semver.forMajorVersion`.

## Plugin Parameters

| Parameter                | Description                                                                                                                                  | Valid Values                                                                                                                                                                                   | Default Value | Optional |
|--------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|----------|
| `semver.stage`           | The stage to be used for calculation                                                                                                         | See [Stage.kt](https://github.com/tcrawford-figure/git-aware-versioning-plugin/blob/main/src/main/kotlin/io/github/tcrawford/versioning/internal/properties/Stage.kt) (case insensitive)       | `auto`        | Yes      |
| `semver.modifier`        | The modifier to be used for calculation                                                                                                      | See [Modifier.kt](https://github.com/tcrawford-figure/git-aware-versioning-plugin/blob/main/src/main/kotlin/io/github/tcrawford/versioning/internal/properties/Modifier.kt) (case insensitive) | `auto`        | Yes      |
| `semver.overrideVersion` | The exact version to use for the build. Must adhere to semantic versioning specification.                                                    | Any valid semantic version                                                                                                                                                                     | `null`        | Yes      |
| `semver.forMajorVersion` | The major version to use as the base for calculating the next major version. Useful for targeting older major versions for support purposes. | Any historically tagged major version. Should be an integer.                                                                                                                                   | `null`        | Yes      |
| `semver.tagPrefix`       | The tag prefix to be used for calculation                                                                                                    | Any valid tag text                                                                                                                                                                             | `v`           | Yes      |

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

# Using for major version
# Assuming latest version is 3.4.5, and latest 2.x version is 2.7.9, we can create 2.7.10 with the following
./gradlew build -Psemver.forMajorVersion=2

# Using tag prefix
./gradlew build -Psemver.tagPrefix=r

```
