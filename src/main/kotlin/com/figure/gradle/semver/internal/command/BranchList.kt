package com.figure.gradle.semver.internal.command

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Ref

internal class BranchList(
    private val git: Git,
) {

    /**
     * Finds an exact branch by name preferring local branches over remote branches, but will return
     * remote branches if the local branch does not exist.
     */
    fun find(containsCommitish: String): Ref? =
        findAll(containsCommitish)?.let { matchingBranches ->
            matchingBranches.find { it.name.contains(Constants.R_HEADS) }
                ?: matchingBranches.find { it.name.contains(Constants.R_REMOTES) }
        }

    /**
     * Only the branches that contain the specified commit-ish as an ancestor are returned.
     *
     * @param containsCommitish a commit ID or ref name
     */
    fun findAll(containsCommitish: String): MutableList<Ref>? =
        git.branchList()
            .setListMode(ListBranchCommand.ListMode.ALL)
            .setContains(containsCommitish)
            .call()
}
