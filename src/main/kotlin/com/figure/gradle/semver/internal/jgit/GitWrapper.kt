package com.figure.gradle.semver.internal.jgit

import org.eclipse.jgit.api.Git
import java.io.File

class GitWrapper(
    private val directory: File? = null,
    private val bare: Boolean = false,
    private val providedGit: Git? = null,
) {
    companion object {
        val init = InitCommandWrapper
        val open = OpenCommand
    }

    val git by lazy {
        providedGit ?: if (directory != null) init(directory, bare) else open()
    }

    fun author(name: String, email: String) {

    }

    val add = AddCommandWrapper(git)
    val checkout = CheckoutCommandWrapper(git)
    val config = ConfigCommand(git)
    val branches = BranchListCommandWrapper(git)
    val commit = CommitCommandWrapper(git)
    val push = PushCommandWrapper(git)
    val remote = RemoteCommand(git)
    val tag = TagCommandWrapper(git)
    val tags = TagListCommandWrapper(git)

    fun close() = git.close()
}
