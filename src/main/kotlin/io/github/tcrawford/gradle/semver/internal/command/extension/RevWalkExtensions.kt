package io.github.tcrawford.gradle.semver.internal.command.extension

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk

internal fun <R> Repository.revWalk(action: (RevWalk) -> R) =
    RevWalk(this).use(action)

internal fun <R> Git.revWalk(action: (RevWalk) -> R) =
    repository.revWalk(action)
