package com.figure.gradle.semver.internal.jgit

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit

class CommitCommandWrapper(
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
