package io.github.tcrawford.gradle.semver.internal.command

import io.github.tcrawford.gradle.semver.internal.command.extension.revWalk
import io.github.tcrawford.gradle.semver.internal.extensions.R_REMOTES_ORIGIN
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref

class BranchList(
    private val git: Git
) {
    fun findDevelopmentBranch(providedDevelopmentBranch: String?): Ref =
        if (!providedDevelopmentBranch.isNullOrBlank()) {
            find(providedDevelopmentBranch)
                ?: error("Could not find provided development branch: $providedDevelopmentBranch")
        } else {
            find("develop")
                ?: find("devel")
                ?: find("dev")
                ?: find("main")
                ?: find("master")
                ?: error("Could not determine default branch. Searched, in order, for: develop, devel, dev, main, master")
        }

    fun findMainBranch(providedMainBranch: String?): Ref =
        if (!providedMainBranch.isNullOrBlank()) {
            find(providedMainBranch) ?: error("Could not find provided main branch: $providedMainBranch")
        } else {
            find("main")
                ?: find("master")
                ?: error("Could not determine main branch. Searched, in order, for: main, master")
        }

    fun exists(branchName: String): Boolean =
        find(branchName) != null

    /**
     * Finds an exact branch by name preferring local branches over remote branches, but will return
     * remote branches if the local branch does not exist.
     */
    private fun find(branchName: String): Ref? =
        findAll(branchName).let { matchingBranches ->
            matchingBranches.find { Constants.R_HEADS in it.name }
                ?: matchingBranches.find { Constants.R_REMOTES in it.name }
        }

    /**
     * Find all branches given the branch name. Can be full or short name.
     *
     * NOTE: this is case-sensitive. If problems happen, consider being case-insensitive.
     */
    private fun findAll(branchName: String): List<Ref> =
        git.branchList()
            .setListMode(ListBranchCommand.ListMode.ALL)
            .call()
            .filter { branchName in it.name }

    fun commitCountBetween(baseBranchName: String, targetBranchName: String): Int {
        // Try to resolve the remote branch first, then fall back to the local branch
        // This should fix situations where you're on the base branch with commits locally.
        // If that's the case, you'll likely get 0 commits between the base and the target branch.
        val baseBranch: ObjectId = git.repository.resolve("$R_REMOTES_ORIGIN/$baseBranchName")
            ?: git.repository.resolve(baseBranchName)

        val targetBranch: ObjectId = git.repository.resolve(targetBranchName)

        return git.revWalk { revWalk ->
            revWalk.apply {
                markStart(parseCommit(targetBranch))
                markUninteresting(parseCommit(baseBranch))
            }

            revWalk.toList().size
        }
    }
}
