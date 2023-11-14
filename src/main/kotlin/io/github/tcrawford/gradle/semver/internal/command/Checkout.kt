package io.github.tcrawford.gradle.semver.internal.command

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref

internal class Checkout(
    private val git: Git
) {
    operator fun invoke(branchName: String, createBranch: Boolean = false): Ref =
        git.checkout()
            .setName(branchName)
            .setCreateBranch(createBranch)
            .call()
}
