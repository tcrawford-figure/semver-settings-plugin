package com.figure.gradle.semver.internal.git

import org.eclipse.jgit.api.Git

fun Git.parentBranch(branchName: String) =
    log()
        .add(repository.resolve(branchName))
        .setMaxCount(1)
        .call()
        .firstOrNull()
        ?.parents
        ?.firstOrNull()
        ?.name
