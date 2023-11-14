package io.github.tcrawford.gradle.semver.internal.command

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit

internal class Log(
    private val git: Git
) {
    fun invoke(): List<RevCommit> =
        git.log().call().toList()
}
