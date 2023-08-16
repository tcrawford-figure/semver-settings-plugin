# Semver Plugin for Gradle Settings

## Plugin Needs

Support the following modes (Both need better names):
- Hash mode
- Commit Increment Mode

Support the following scenarios:
- Ensure you can support configuration cache early on in development!!!
- No remote set
- No semver tags or tags at all
- Add suggestions if no git directory can be found
- `+DIRTY` - dirty mode, where changes have been made, but no commit made yet. Applies to both modes above
- Somehow support tests in GHA that are currently not possible. More documentation can be found in current plugin.

### Library Considerations

- kotlin-semver
- Some alternative Result library (consider current and future support)
  - Arrow (much smaller footprint now)
  - kotlin-result: https://github.com/michaelbull/kotlin-result
  - Result: https://github.com/kittinunf/Result

### Commit Increment Mode Resources

Get branch that commit belongs to (good for GitHub Actions):

Note: This accepts short or long hash

```shell
git branch --contains b774d50
```

Get nearest parent:

```shell
git show-branch -a | sed "s/].*//" | grep "\*" | grep -v "$(git rev-parse --abbrev-ref HEAD)" | head -n1 | sed "s/^.*\[//"
```

Get commits since parent:

```shell
git rev-list --count feat-1..sub-feat-1
```

#### Validate the following scenarios

- Branch rebase (should be perfectly fine)
- Merge --no-ff (what does this actually look like from a git log perspective?)
- Merge --ff (I think that's the flag, but it should be default?)

### Testing

- To simulate GitHub Actions, you can try checking out the HEAD commit in a test and resolving from there

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
