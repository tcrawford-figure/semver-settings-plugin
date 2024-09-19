This plugin defines four Gradle parameter properties that the plugin uses to
determine the next version based on the latest tag: `semver.stage`,
`semver.modifier`, `semver.overrideVersion`, and `semver.tagPrefix`.

- `semver.stage` - indicates the stage to be used for calculation
- `semver.modifier` - indicates the modifier to be used for calculation
- `semver.overrideVersion` - indicates the exact version to be used for the
  build. Must adhere to semantic versioning specification.
- `semver.tagPrefix` - indicates the tag prefix to be used for calculation

| Parameter                | Valid Values                                                                                                                                                                                   | Default Value | Optional |
|--------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|----------|
| `semver.stage`           | See [Stage.kt](https://github.com/tcrawford-figure/git-aware-versioning-plugin/blob/main/src/main/kotlin/io/github/tcrawford/versioning/internal/properties/Stage.kt) (case insensitive)       | `auto`        | Yes      |
| `semver.modifier`        | See [Modifier.kt](https://github.com/tcrawford-figure/git-aware-versioning-plugin/blob/main/src/main/kotlin/io/github/tcrawford/versioning/internal/properties/Modifier.kt) (case insensitive) | `auto`        | Yes      |
| `semver.overrideVersion` | Any valid semantic version                                                                                                                                                                     | `null`        | Yes      |
| `semver.tagPrefix`       | Any valid tag text                                                                                                                                                                             | `v`           | Yes      |

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
