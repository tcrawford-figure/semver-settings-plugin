package com.figure.gradle.semver.internal.jgit

import org.eclipse.jgit.api.Git
import java.io.File

class GitWrapper(
    private val directory: File? = null,
    private val providedGit: Git? = null,
) {
    companion object {
        val init = InitCommandWrapper
        val open = OpenCommand
    }

    val git by lazy {
        providedGit ?: run {
            requireNotNull(directory) { "A git directory must be provided" }
            init(directory)
        }
    }

    val add = AddCommandWrapper(git)
    val commit = CommitCommandWrapper(git)
    val push = PushCommandWrapper(git)
    val tag = TagCommandWrapper(git)
    val tags = TagListCommandWrapper(git)
}
