package com.figure.gradle.semver.internal.jgit

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File

object OpenCommand {
    operator fun invoke(): Git = Git(
        FileRepositoryBuilder()
            .readEnvironment()
            .findGitDir()
            .build()
    )

    fun byRepoDir(repoDir: String): Git = Git.open(File(repoDir))

    fun byGitDir(gitDir: String): Git = Git(
        FileRepositoryBuilder()
            .setGitDir(File(gitDir))
            .readEnvironment()
            .findGitDir()
            .build()
    )
}
