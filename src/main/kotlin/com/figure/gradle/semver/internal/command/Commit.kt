package com.figure.gradle.semver.internal.command

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit

internal class Commit(
    private val git: Git,
) {
    operator fun invoke(
        message: String,
        allowEmptyCommit: Boolean = false,
    ): RevCommit =
        git.commit()
            .setMessage(message)
            .setAllowEmpty(allowEmptyCommit)
            .call()
}
