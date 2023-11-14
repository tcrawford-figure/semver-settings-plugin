package io.github.tcrawford.gradle.semver.internal.extensions

import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Ref

internal const val R_REMOTES_ORIGIN = "${Constants.R_REMOTES}${Constants.DEFAULT_REMOTE_NAME}"

private val validCharacters: Regex = """[^0-9A-Za-z\-_.]+""".toRegex()

internal fun Ref.sanitizedWithoutPrefix(): String =
    name.trim()
        .lowercase()
        .replace(Constants.R_HEADS, "")
        .replace("$R_REMOTES_ORIGIN/", "")
        .removePrefix("/")
        .replace(validCharacters, "-")
