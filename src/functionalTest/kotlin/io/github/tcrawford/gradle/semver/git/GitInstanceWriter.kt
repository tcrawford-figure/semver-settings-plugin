package io.github.tcrawford.gradle.semver.git

import io.github.tcrawford.gradle.semver.internal.command.KGit

class GitInstanceWriter(
    private val localGit: KGit,
    private val gitActionsConfig: GitActionsConfig,
) {
    fun write(printGitObjects: Boolean) {
        gitActionsConfig.actions.forEach { action -> action.execute(localGit) }

        localGit.push.all()

        localGit.print.commits(printGitObjects)
        localGit.print.refs(printGitObjects)
        localGit.print.tags(printGitObjects)
    }
}
