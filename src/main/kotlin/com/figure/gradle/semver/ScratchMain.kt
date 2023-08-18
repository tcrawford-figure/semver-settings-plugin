package com.figure.gradle.semver

import com.figure.gradle.semver.internal.jgit.GitWrapper
import com.figure.gradle.semver.internal.jgit.revWalk
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit

fun main(args: Array<String>) {
    val git = GitWrapper.open()

    println(git.latestCommitOnBranch().name)
}

fun Git.latestCommitOnBranch(): RevCommit =
    revWalk { walk ->
        val branchRef = repository.findRef(repository.branch)
        walk.parseCommit(branchRef.objectId)
    }
