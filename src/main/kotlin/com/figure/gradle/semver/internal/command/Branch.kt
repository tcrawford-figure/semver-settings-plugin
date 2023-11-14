package com.figure.gradle.semver.internal.command

import com.figure.gradle.semver.internal.command.extension.revWalk
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit

internal class Branch(
    private val git: Git,
    private val branchList: BranchList
) {
    private val githubHeadRef: String = "GITHUB_HEAD_REF"

    val shortName: String
        get() = git.repository.branch

    val fullName: String
        get() = git.repository.fullBranch

    val branchRef: Ref
        get() = git.repository.findRef(shortName)

    val headRef: Ref
        get() = git.repository.exactRef(Constants.HEAD)

    val headCommit: RevCommit
        get() = git.revWalk { it.parseCommit(branchRef.objectId) }

    fun currentRef(forTesting: Boolean = false): Ref =
        if (forTesting) {
            branchRef
        } else {
            git.repository.findRef(System.getenv(githubHeadRef) ?: shortName)
        }

    fun isOnMainBranch(forTesting: Boolean = false): Boolean =
        currentRef(forTesting).name == branchList.mainBranch.name

    fun isOnDevelopmentBranch(forTesting: Boolean = false): Boolean =
        currentRef(forTesting).name == branchList.developmentBranch.name

    fun create(branchName: String): Ref =
        git.branchCreate()
            .setName(branchName)
            .call()

    fun delete(vararg branchNames: String) =
        git.branchDelete()
            .setBranchNames(*branchNames)
            .call()
}
