package io.github.tcrawford.gradle.semver.testkit

sealed class Action

class CheckoutAction : Action() {
    var branch: String = ""
}

class CommitAction : Action() {
    var message: String = ""
    var tag: String = ""
}

class RepositoryConfig {
    lateinit var initialBranch: String
    val actions = mutableListOf<Action>()

    fun actions(config: Actions.() -> Unit) {
        val actionObject = Actions()
        actionObject.config()
        actions.addAll(actionObject.actions)
    }
}

class Actions {
    val actions = mutableListOf<Action>()

    fun checkout(config: CheckoutAction.() -> Unit) {
        val checkoutAction = CheckoutAction()
        checkoutAction.config()
        actions.add(checkoutAction)
    }

    fun checkout(branch: String) {
        val checkoutAction = CheckoutAction()
        checkoutAction.branch = branch
        actions.add(checkoutAction)
    }

    fun commit(config: CommitAction.() -> Unit) {
        val commitAction = CommitAction()
        commitAction.config()
        actions.add(commitAction)
    }

    fun commit(message: String = "Empty Commit", tag: String = "") {
        val commitAction = CommitAction()
        commitAction.message = message
        commitAction.tag = tag
        actions.add(commitAction)
    }
}

fun repositoryConfig(config: RepositoryConfig.() -> Unit): RepositoryConfig {
    val repositoryConfig = RepositoryConfig()
    repositoryConfig.config()
    return repositoryConfig
}

fun main() {
    val config = repositoryConfig {
        val mainBranch = "main"
        val developmentBranch = "develop"
        val featureBranch = "my-awesome-feature"
        actions {
            checkout {
                branch = mainBranch
            }
            commit {
                message = "my message"
                tag = "1.0.0"
            }
            commit {
                message = "another commit"
                tag = "1.0.1"
            }
            checkout {
                branch = developmentBranch
            }
            commit {
                message = "another commit"
            }
            checkout {
                branch = featureBranch
            }
            commit {
                message = "feature branch commit"
            }
        }
    }

    // Iterating over actions and reacting to specific types
    config.actions.forEach { action ->
        when (action) {
            is CheckoutAction -> {
                println("Checkout action for branch: ${action.branch}")
                // React to checkout action
            }
            is CommitAction -> {
                println("Commit action with message: ${action.message}, tag: ${action.tag}")
                // React to commit action
            }
        }
    }
}
