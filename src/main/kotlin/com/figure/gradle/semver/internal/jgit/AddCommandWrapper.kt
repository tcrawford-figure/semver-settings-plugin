package com.figure.gradle.semver.internal.jgit

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.dircache.DirCache

class AddCommandWrapper(
    private val git: Git,
) {
    operator fun invoke(filename: String): DirCache? =
        git.add().addFilepattern(filename).call()

    fun all(): DirCache? =
        git.add().addFilepattern(".").call()
}
