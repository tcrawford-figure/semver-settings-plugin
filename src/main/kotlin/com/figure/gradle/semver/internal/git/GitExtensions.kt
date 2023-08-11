package com.figure.gradle.semver.internal.git

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk

internal val Repository.headRef: ObjectId
    get() = exactRef(Constants.HEAD).objectId

context(Git)
internal val RevWalk.headCommit: RevCommit
    get() = parseCommit(repository.headRef)

internal fun Git.commitsSinceBranchPoint(): Int =
    RevWalk(repository).use { revWalk ->
        revWalk.markStart(revWalk.headCommit)
        revWalk.indexOfFirst { commit -> branchList().setContains(commit.name).call().size > 1 }
    }
