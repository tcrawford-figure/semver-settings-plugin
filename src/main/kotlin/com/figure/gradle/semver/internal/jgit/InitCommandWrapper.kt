package com.figure.gradle.semver.internal.jgit

import org.eclipse.jgit.api.Git
import java.io.File

object InitCommandWrapper {
    operator fun invoke(
        directory: File,
        bare: Boolean = false,
        initialBranch: String = "main"
    ): Git = Git.init()
        .setDirectory(directory)
        .setBare(bare)
        .setInitialBranch(initialBranch)
        .call()
}
