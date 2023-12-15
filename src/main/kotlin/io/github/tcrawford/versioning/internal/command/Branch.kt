package io.github.tcrawford.versioning.internal.command

import io.github.tcrawford.versioning.internal.command.extension.revWalk
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit

class Branch(
    private val git: Git,
    private val branchList: BranchList,
) {
    private val githubHeadRef: String = "GITHUB_HEAD_REF"

    private val shortName: String
        get() = git.repository.branch

    val fullName: String
        get() = git.repository.fullBranch

    private val branchRef: Ref
        get() = git.repository.findRef(shortName)

    val headRef: Ref?
        get() = git.repository.exactRef(Constants.HEAD)

    val headCommit: RevCommit
        get() = git.revWalk { it.parseCommit(branchRef.objectId) }

    fun currentRef(forTesting: Boolean = false): Ref =
        if (forTesting) {
            branchRef
        } else {
            git.repository.findRef(System.getenv(githubHeadRef) ?: shortName)
        }

    fun isOnMainBranch(providedMainBranch: String? = null, forTesting: Boolean = false): Boolean =
        currentRef(forTesting).name == branchList.findMainBranch(providedMainBranch).name

    fun create(branchName: String): Ref =
        git.branchCreate()
            .setName(branchName)
            .call()

    fun delete(vararg branchNames: String): List<String> =
        git.branchDelete()
            .setBranchNames(*branchNames)
            .setForce(true)
            .call()
}
