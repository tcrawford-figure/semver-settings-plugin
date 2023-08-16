package com.figure.gradle.semver

import com.figure.gradle.semver.internal.git.jgit.openNearestGitRepo
import com.figure.gradle.semver.internal.git.jgit.showBranch

fun main(args: Array<String>) {
    val git = openNearestGitRepo()

    git.showBranch()
}
