package com.figure.gradle.semver.internal.jgit

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.PushResult
import org.eclipse.jgit.transport.RefSpec

class PushCommandWrapper(
    private val git: Git,
) {
    operator fun invoke(): MutableIterable<PushResult>? =
        git.push().call()

    fun branch(branch: String): MutableIterable<PushResult>? =
        git.push()
            .setRefSpecs(RefSpec("refs/heads/$branch:refs/heads/$branch"))
            .call()

    fun tag(tag: String): MutableIterable<PushResult>? =
        git.push()
            .setRefSpecs(RefSpec("refs/tags/$tag:refs/tags/$tag"))
            .call()

    fun allBranches(): MutableIterable<PushResult>? =
        git.push()
            .setPushAll() // Push all branches under refs/heads/*
            .call()

    fun allTags(): MutableIterable<PushResult>? =
        git.push()
            .setPushTags() // Push all tags under refs/tags/*
            .call()

    fun all(): MutableIterable<PushResult>? =
        git.push()
            .setPushAll() // Push all branches under refs/heads/*
            .setPushTags() // Push all tags under refs/tags/*
            .call()
}
