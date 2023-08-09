package com.figure.gradle.semver

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import java.io.File

fun main(args: Array<String>) {
    val git = Git.open(File("/Users/tylercrawford/dev/playgrounds/semver-settings-plugin/.git"))

    println(git.commitsSinceBranchPoint())
    println(git.commitsSinceBranchPointGpt())
    println(git.commitsSinceBranchPointGpt2())
    println(git.commitsSinceBranchPointTyler())
}

fun Git.commitsSinceBranchPoint(): Int {
    val commits: List<RevCommit> = log().call().toList()

    val index = commits
        .map { branchList().setContains(it.name).call() }
        .indexOfFirst { it.size > 1 }

    return commits.take(index).count()
}

fun Git.commitsSinceBranchPointTyler(): Int =
    RevWalk(repository).use { revWalk ->
        // revWalk.markStart(revWalk.parseCommit(repository.resolve(Constants.HEAD)))
        revWalk.indexOfFirst { commit -> branchList().setContains(commit.name).call().size > 1 }
    }

fun Git.commitsSinceBranchPointGpt(): Int {
    val revWalk = RevWalk(this.repository)
    val commits = mutableListOf<RevCommit>()

    revWalk.markStart(revWalk.parseCommit(this.repository.resolve("HEAD")))

    for (commit in revWalk) {
        commits.add(commit)
        if (this.branchList().setContains(commit.name).call().size > 1) {
            break
        }
    }

    revWalk.dispose()

    return commits.size - 1
}

fun Git.commitsSinceBranchPointGpt2(): Int =
    RevWalk(this.repository).use { revWalk ->
        generateSequence(revWalk.parseCommit(this.repository.resolve("HEAD"))) { commit ->
            revWalk.parseCommit(commit.parents.firstOrNull())
        }.takeWhile { commit ->
            this.branchList().setContains(commit.name).call().size <= 1
        }.count()
    }
