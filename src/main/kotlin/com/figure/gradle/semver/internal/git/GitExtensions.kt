package com.figure.gradle.semver.internal.git

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

internal fun openNearestGitRepo(): Git =
    Git(
        FileRepositoryBuilder()
            .readEnvironment()
            .findGitDir()
            .build()
    )

internal val Repository.headRef: ObjectId
    get() = exactRef(Constants.HEAD).objectId

context(Git)
internal val RevWalk.headCommit: RevCommit
    get() = parseCommit(repository.headRef)

/**
 * Gets the number of commits since the branch point.
 * This should account
 */
internal fun Git.commitsSinceBranchPoint(): Int =
    RevWalk(repository).use { revWalk ->
        revWalk.markStart(revWalk.headCommit)
        revWalk.indexOfFirst { commit ->
            branchList()
                .setContains(commit.name)
                .call()
                .map { it.name }
                .filterNot { it == Constants.HEAD }
                .size > 1
        }
    }
