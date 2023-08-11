package com.figure.gradle.semver

import com.figure.gradle.semver.internal.git.commitsSinceBranchPoint
import com.figure.gradle.semver.internal.git.openNearestGitRepo

fun main(args: Array<String>) {
    val git = openNearestGitRepo()
    log.lifecycle("Commits since branch point: ${git.commitsSinceBranchPoint()}")
}
