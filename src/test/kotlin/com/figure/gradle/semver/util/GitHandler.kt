package com.figure.gradle.semver.util

import com.figure.gradle.semver.internal.jgit.GitWrapper
import com.figure.gradle.semver.log
import io.github.z4kn4fein.semver.Inc
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.inc
import io.github.z4kn4fein.semver.toVersion
import org.eclipse.jgit.api.Git
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GitHandler(
    localRepoDir: File,
    remoteRepoDir: File,
    private var startingTag: Version = "1.0.0".toVersion(),
) {
    private val localGitWrapper = GitWrapper(localRepoDir)
    private val remoteGitWrapper = GitWrapper(remoteRepoDir, bare = true)

    init {
        localGitWrapper.config.author("Anita Bath", "anita@spla.sh")
        localGitWrapper.remote.add(remoteGitWrapper.git)
    }

    fun createCommits(numberOfCommits: Int): GitHandler {
        repeat(numberOfCommits) { localGitWrapper.commit("Empty commit", allowEmptyCommit = true) }
        return this
    }

    fun createCommitsWithTags(numberOfCommits: Int, incrementBy: Inc = Inc.MINOR): GitHandler {
        repeat(numberOfCommits) {
            startingTag = startingTag.inc(incrementBy)

            localGitWrapper.commit("Empty commit", allowEmptyCommit = true)
            localGitWrapper.tag("v${startingTag}")
        }
        return this
    }

    fun pushAll(): GitHandler {
        localGitWrapper.push.all()
        return this
    }

    fun createBranch(branchName: String): GitHandler {
        localGitWrapper.checkout(branchName, createBranch = true)
        return this
    }

    fun logLocalGitObjects(): GitHandler {
        logGitObjects(localGitWrapper.git)
        return this
    }

    fun logRemoteGitObjects(): GitHandler {
        logGitObjects(remoteGitWrapper.git)
        return this
    }

    fun close() {
        localGitWrapper.close()
        remoteGitWrapper.close()
    }

    private fun logGitObjects(git: Git) {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        git.log().call().forEachIndexed { index, commit ->
            val commitTime = commit.commitTime.toLocalDateTime()
            log.lifecycle("Commit ${index + 1}:")
            log.lifecycle("  - Name: ${commit.name}")
            log.lifecycle("  - Commit Time: ${commitTime.format(dateTimeFormatter)}")
            log.lifecycle("  - Message: ${commit.fullMessage}")
        }

        log.lifecycle("Refs:")
        git.repository.refDatabase.refs.forEach { ref ->
            log.lifecycle("  - ${ref.name}")
        }
    }

    private fun Int.toLocalDateTime(): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(this.toLong()), ZoneId.systemDefault())
    }
}
