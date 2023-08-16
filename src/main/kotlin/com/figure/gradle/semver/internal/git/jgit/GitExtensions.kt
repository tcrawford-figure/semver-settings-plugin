package com.figure.gradle.semver.internal.git.jgit

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

fun Git.showBranch() {
    val refs: List<Ref> = branchList().call()

    refs.forEach { ref ->
        val objectId = ref.objectId
        val commit = log().add(objectId).setMaxCount(1).call().iterator().next()
        println("${ref.name}: ${commit.abbreviate(8).name()}")
    }
}

fun openNearestGitRepo() = Git(
    FileRepositoryBuilder()
        .readEnvironment()
        .findGitDir()
        .build()
)
