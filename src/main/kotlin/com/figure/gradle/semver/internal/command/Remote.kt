package com.figure.gradle.semver.internal.command

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.transport.RemoteConfig
import org.eclipse.jgit.transport.URIish
import java.net.URL

internal class Remote(
    private val git: Git,
) {
    fun add(
        remoteGit: Git,
        remoteName: String = Constants.DEFAULT_REMOTE_NAME,
    ): RemoteConfig =
        git.remoteAdd()
            .setName(remoteName)
            .setUri(URIish(remoteGit.repository.directory.toURI().toURL()))
            .call()

    fun add(
        remoteUri: String,
        remoteName: String = Constants.DEFAULT_REMOTE_NAME,
    ): RemoteConfig =
        git.remoteAdd()
            .setName(remoteName)
            .setUri(URIish(remoteUri))
            .call()

    fun add(
        remoteUri: URL,
        remoteName: String = Constants.DEFAULT_REMOTE_NAME,
    ): RemoteConfig =
        git.remoteAdd()
            .setName(remoteName)
            .setUri(URIish(remoteUri))
            .call()
}
