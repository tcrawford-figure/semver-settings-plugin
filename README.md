# Semver Plugin for Gradle Settings

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
