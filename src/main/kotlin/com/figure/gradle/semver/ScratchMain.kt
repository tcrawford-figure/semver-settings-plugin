package com.figure.gradle.semver

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit
import java.io.File

fun main(args: Array<String>) {
    val git = Git.open(File("/Users/tylercrawford/dev/playgrounds/semver-settings-plugin/.git"))

    val commits: List<RevCommit> = git.log().call().toList()

    val index = commits
        .map { git.branchList().setContains(it.name).call() }
        .indexOfFirst { it.size > 1 }

    val counts = commits.take(index).count()

    println(counts)
}

fun Git.branchesCount(revCommit: RevCommit): Int =
    branchList().setContains(revCommit.name).call().size
