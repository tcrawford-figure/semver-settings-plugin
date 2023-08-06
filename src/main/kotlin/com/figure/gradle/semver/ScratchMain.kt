package com.figure.gradle.semver

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevWalk
import java.io.File

fun main(args: Array<String>) {
    val git = Git.open(File("/Users/tylercrawford/dev/playgrounds/semver-settings-plugin/.git"))

    val currentBranch = git.repository.branch
    println("Current branch: $currentBranch")

    val parentBranch = git.getBranchName()
    println("Parent branch: $parentBranch")
}

fun Git.getBranchName(): String {
    val revWalk = RevWalk(repository)

    val head = revWalk.parseCommit(repository.findRef("HEAD").objectId)
    val parents = head.parents

    if (parents.isEmpty()) {
        return ""
    }

    val parent = parents[0]
    return parent.name
}
