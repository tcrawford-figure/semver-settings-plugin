package com.figure.gradle.semver.internal.jgit

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref

class CheckoutCommandWrapper(
    private val git: Git
) {
    operator fun invoke(branchName: String, createBranch: Boolean = false): Ref =
        git.checkout()
            .setName(branchName)
            .setCreateBranch(createBranch)
            .call()
}
