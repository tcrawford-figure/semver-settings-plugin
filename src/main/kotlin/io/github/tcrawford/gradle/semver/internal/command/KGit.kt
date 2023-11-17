package io.github.tcrawford.gradle.semver.internal.command

import org.eclipse.jgit.api.Git
import java.io.File

internal class KGit(
    directory: File? = null,
    initializeRepo: InitializeRepo? = null,
) {
    val git: Git by lazy {
        when {
            directory != null && initializeRepo != null -> init(
                directory,
                bare = initializeRepo.bare,
                initialBranch = initializeRepo.initialBranch
            )

            directory != null -> open(directory)
            else -> open()
        }
    }

    companion object {
        val init = Init
        val open = Open
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

internal data class InitializeRepo(
    val bare: Boolean,
    val initialBranch: String,
)
