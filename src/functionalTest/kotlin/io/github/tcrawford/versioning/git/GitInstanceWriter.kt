package io.github.tcrawford.versioning.git

import io.github.tcrawford.versioning.internal.command.KGit

class GitInstanceWriter(
    private val localGit: KGit,
    private val gitActionsConfig: GitActionsConfig,
) {
    fun write(printGitObjects: Boolean) {
        gitActionsConfig.actions
            .filterNot { it is RemovalAction }
            .forEach { action -> action.execute(localGit) }

        localGit.push.all()

        // This will run any removal actions
        gitActionsConfig.actions
            .filterIsInstance<RemovalAction>()
            .forEach { action -> action.execute(localGit) }

        localGit.print.commits(printGitObjects)
        localGit.print.refs(printGitObjects)
        localGit.print.tags(printGitObjects)
    }
}
