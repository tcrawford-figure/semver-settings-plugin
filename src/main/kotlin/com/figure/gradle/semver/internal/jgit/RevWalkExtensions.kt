package com.figure.gradle.semver.internal.jgit

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk

fun <R> Repository.revWalk(action: (RevWalk) -> R) =
    RevWalk(this).use(action)

fun <R> Git.revWalk(action: (RevWalk) -> R) =
    repository.revWalk(action)
