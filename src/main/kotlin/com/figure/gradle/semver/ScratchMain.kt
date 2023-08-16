package com.figure.gradle.semver

import com.figure.gradle.semver.internal.git.openNearestGitRepo

fun main(args: Array<String>) {
    val git = openNearestGitRepo()
}
