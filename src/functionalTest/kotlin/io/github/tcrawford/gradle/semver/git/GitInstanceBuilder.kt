package io.github.tcrawford.gradle.semver.git

fun gitInstance(block: GitInstance.Builder.() -> Unit): GitInstance =
    GitInstance.Builder().apply(block).build()

data class GitInstance(
    val debugging: Boolean,
    val initialBranch: String,
    val actions: GitActionsConfig,
) {
    class Builder {
        var debugging: Boolean = false
        var initialBranch: String = "main"
        var actions: GitActionsConfig = GitActionsConfig()

        fun actions(config: Actions.() -> Unit): GitActionsConfig {
            actions.actions(config)
            return actions
        }

        fun build() = GitInstance(
            debugging = debugging,
            initialBranch = initialBranch,
            actions = actions,
        )
    }
}
