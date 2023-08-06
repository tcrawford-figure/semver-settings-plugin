package com.figure.gradle.semver

import org.eclipse.jgit.api.Git
import java.io.File

fun main(args: Array<String>) {
    val git = Git.open(File("/Users/tylercrawford/dev/playgrounds/semver-settings-plugin/.git"))

    val currentBranch = git.repository.branch
    println("Current branch: $currentBranch")

    val commits = git.log().call().toList()

    val uniqueBranches = commits
        .map { git.branchList().setContains(it.name).call() }
        .flatten()
        .toSet()

    println(uniqueBranches)
}
