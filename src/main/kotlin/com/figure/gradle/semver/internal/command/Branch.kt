package com.figure.gradle.semver.internal.command

import com.figure.gradle.semver.internal.command.extension.revWalk
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit

internal class Branch(
    private val git: Git,
) {
    val shortName: String = git.repository.branch
    val fullName: String = git.repository.fullBranch

    val branchRef: Ref = git.repository.findRef(shortName)

    val headRef: Ref = git.repository.exactRef(Constants.HEAD)
    val headCommit: RevCommit = git.revWalk { it.parseCommit(branchRef.objectId) }

    fun create(branchName: String): Ref =
        git.branchCreate()
            .setName(branchName)
            .call()

    fun delete(vararg branchNames: String) =
        git.branchDelete()
            .setBranchNames(*branchNames)
            .call()
}
