package com.figure.gradle.semver

import com.figure.gradle.semver.internal.command.KGit
import com.figure.gradle.semver.internal.command.extension.revWalk
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit

fun main(args: Array<String>) {
    val kgit = KGit()

    println(kgit.git.latestCommitOnBranch().name)
}

fun Git.latestCommitOnBranch(): RevCommit =
    revWalk { walk ->
        val branchRef = repository.findRef(repository.branch)
        walk.parseCommit(branchRef.objectId)
    }
