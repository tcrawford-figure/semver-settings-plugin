package io.github.tcrawford.versioning.git

import io.github.tcrawford.versioning.internal.command.KGit
import io.github.tcrawford.versioning.projects.AbstractProject
import io.github.tcrawford.versioning.util.resolveResource

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
    val script: Script,
    val project: AbstractProject,
    val arguments: List<String>,
) : Action {
    override fun execute(git: KGit) {
        val scriptFile = resolveResource("scripts/${script.scriptFileName}")
        val projectPath = project.gradleProject.rootDir.absolutePath

        val processBuilder = ProcessBuilder()
        processBuilder.command("bash", scriptFile.absolutePath, projectPath, *arguments.toTypedArray())
        processBuilder.redirectErrorStream(true)
        val process = processBuilder.start()
        process.waitFor()
    }
}

class GitActionsConfig(
    private val project: AbstractProject,
) {
    val actions = mutableListOf<Action>()

    fun actions(config: Actions.() -> Unit) {
        val actionObject = Actions(project)
        actionObject.config()
        actions.addAll(actionObject.actionsToRun)
    }
}

class Actions(
    private val project: AbstractProject,
) {
    internal val actionsToRun = mutableListOf<Action>()

    fun checkout(branch: String) {
        val checkoutAction = CheckoutAction(branch)
        actionsToRun.add(checkoutAction)
    }

    fun commit(message: String = "Empty Commit", tag: String = "") {
        val commitAction = CommitAction(message, tag)
        actionsToRun.add(commitAction)
    }

    fun runScript(script: Script, vararg arguments: String) {
        val runScriptAction = RunScriptAction(script, project, arguments.toList())
        actionsToRun.add(runScriptAction)
    }
}

enum class Script(
    val scriptFileName: String,
) {
    CREATE_BISECTING_STATE("create_bisecting_state.sh"),
    CREATE_CHERRY_PICKING_STATE("create_cherry_picking_state.sh"),
    CREATE_DETACHED_HEAD_STATE("create_detached_head_state.sh"),
    CREATE_MERGING_STATE("create_merging_state.sh"),
    CREATE_REBASING_STATE("create_rebasing_state.sh"),
    CREATE_REVERTING_STATE("create_reverting_state.sh"),
}
