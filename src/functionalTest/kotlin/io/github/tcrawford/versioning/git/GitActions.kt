package io.github.tcrawford.versioning.git

import io.github.tcrawford.versioning.internal.command.KGit
import java.io.File

sealed interface Action {
    fun execute(git: KGit)
}

data class CheckoutAction(
    val branch: String,
) : Action {
    override fun execute(git: KGit) {
        if (git.branch.headRef?.objectId?.name == null) {
            git.commit("Initial commit", allowEmptyCommit = true)
        }

        if (git.branches.exists(branch)) {
            git.checkout(branch)
        } else {
            git.checkout(branch, createBranch = true)
        }
    }
}

data class CommitAction(
    val message: String = "Empty Commit",
    val tag: String? = null,
) : Action {
    override fun execute(git: KGit) {
        git.commit(message, true)

        if (!tag.isNullOrBlank()) {
            git.tag("v$tag")
        }
    }
}

data class RunScriptAction(
    val script: File,
    val arguments: List<String>,
) : Action {
    override fun execute(git: KGit) {
        val processBuilder = ProcessBuilder()
        processBuilder.command("bash", script.absolutePath, *arguments.toTypedArray())
        processBuilder.redirectErrorStream(true)
        val process = processBuilder.start()
        process.waitFor()
    }
}

class GitActionsConfig {
    val actions = mutableListOf<Action>()

    fun actions(config: Actions.() -> Unit) {
        val actionObject = Actions()
        actionObject.config()
        actions.addAll(actionObject.actionsToRun)
    }
}

class Actions {
    internal val actionsToRun = mutableListOf<Action>()

    fun checkout(branch: String) {
        val checkoutAction = CheckoutAction(branch)
        actionsToRun.add(checkoutAction)
    }

    fun commit(message: String = "Empty Commit", tag: String = "") {
        val commitAction = CommitAction(message, tag)
        actionsToRun.add(commitAction)
    }

    fun runScript(script: File, vararg arguments: String) {
        val runScriptAction = RunScriptAction(script, arguments.toList())
        actionsToRun.add(runScriptAction)
    }
}
