package io.github.tcrawford.versioning.git

import io.github.tcrawford.versioning.projects.AbstractProject

fun gitInstance(project: AbstractProject, block: GitInstance.Builder.() -> Unit): GitInstance =
    GitInstance.Builder(project).apply(block).build()

data class GitInstance(
    val project: AbstractProject,
    val debugging: Boolean,
    val initialBranch: String,
    val actions: GitActionsConfig,
) {
    class Builder(
        private val project: AbstractProject,
    ) {
        var debugging: Boolean = false
        var initialBranch: String = "main"
        var actions: GitActionsConfig = GitActionsConfig(project)

        fun actions(config: Actions.() -> Unit): GitActionsConfig {
            actions.actions(config)
            return actions
        }

        fun build() = GitInstance(
            project = project,
            debugging = debugging,
            initialBranch = initialBranch,
            actions = actions,
        )
    }
}
