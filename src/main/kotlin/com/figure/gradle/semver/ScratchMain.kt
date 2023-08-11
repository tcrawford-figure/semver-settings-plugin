package com.figure.gradle.semver

import org.eclipse.jgit.api.Git
import java.io.File

fun main(args: Array<String>) {
    val git = Git.open(File("/Users/tylercrawford/dev/figure/stream-data/.git"))

    git.repository.refDatabase.refs.forEach {
        println(it.name)
    }

    // println(git.commitsSinceBranchPoint())
}
