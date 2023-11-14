package com.figure.gradle.semver.internal.command

import org.eclipse.jgit.api.Git
import java.io.File

internal class KGit(
    private val directory: File? = null,
    private val bare: Boolean = false,
    private val providedGit: Git? = null
) {
    companion object {
        val init = Init
        val open = Open
    }

    val git by lazy {
        providedGit ?: if (directory != null) init(directory, bare) else open()
    }

    val add = Add(git)
    val checkout = Checkout(git)
    val config = Config(git)
    val branches = BranchList(git)
    val branch = Branch(git, branches)
    val commit = Commit(git)
    val log = Log(git)
    val push = Push(git)
    val remote = Remote(git)
    val tag = Tag(git)
    val tags = TagList(git)

    fun close() = git.close()
}
