package io.github.tcrawford.gradle.semver.internal.command

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.dircache.DirCache

internal class Add(
    private val git: Git
) {
    operator fun invoke(filename: String): DirCache? =
        git.add().addFilepattern(filename).call()

    fun all(): DirCache? =
        git.add().addFilepattern(".").call()
}
