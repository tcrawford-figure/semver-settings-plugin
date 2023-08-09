package com.figure.gradle.semver

import com.figure.gradle.semver.internal.git.commitsSinceBranchPoint
import org.eclipse.jgit.api.Git
import java.io.File

fun main(args: Array<String>) {
    val git = Git.open(File("/Users/tylercrawford/dev/playgrounds/semver-settings-plugin/.git"))

    println(git.commitsSinceBranchPoint())
}
