package com.figure.gradle.semver.util

import com.figure.gradle.semver.internal.command.KGit
import io.github.z4kn4fein.semver.Inc
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.inc
import io.github.z4kn4fein.semver.toVersion
import org.eclipse.jgit.api.Git
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

private val log: Logger = Logging.getLogger(Logger.ROOT_LOGGER_NAME)

class GitHandler(
    localRepoDir: File,
    remoteRepoDir: File,
    private var startingTag: Version = "1.0.0".toVersion()
) {
    private val localKGit = KGit(localRepoDir)
    private val remoteKGit = KGit(remoteRepoDir, bare = true)

    init {
        localKGit.config.author("Anita Bath", "anita@spla.sh")
        localKGit.remote.add(remoteKGit.git)
    }

    fun createCommits(numberOfCommits: Int): GitHandler {
        repeat(numberOfCommits) { localKGit.commit("Empty commit", allowEmptyCommit = true) }
        return this
    }

    fun createCommitsWithTags(numberOfCommits: Int, incrementBy: Inc = Inc.MINOR): GitHandler {
        repeat(numberOfCommits) {
            startingTag = startingTag.inc(incrementBy)

            localKGit.commit("Empty commit", allowEmptyCommit = true)
            localKGit.tag("v$startingTag")
        }
        return this
    }

    fun pushAll(): GitHandler {
        localKGit.push.all()
        return this
    }

    fun createBranch(branchName: String): GitHandler {
        localKGit.checkout(branchName, createBranch = true)
        return this
    }

    fun logLocalGitObjects(): GitHandler {
        logGitObjects(localKGit.git)
        return this
    }

    fun logRemoteGitObjects(): GitHandler {
        logGitObjects(remoteKGit.git)
        return this
    }

    fun close() {
        localKGit.close()
        remoteKGit.close()
    }

    private fun logGitObjects(git: Git) {
        // val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        log.lifecycle("Commits:")
        git.log().call().forEach { commit ->
            log.lifecycle("  - $commit")
        }

        // git.log().call().forEachIndexed { index, commit: RevCommit ->
        //     val commitTime = commit.commitTime.toLocalDateTime()
        //     log.lifecycle("Commit ${index + 1}:")
        //     log.lifecycle("  - Name: ${commit.name}")
        //     log.lifecycle("  - Commit Time: ${commitTime.format(dateTimeFormatter)}")
        //     log.lifecycle("  - Message: ${commit.fullMessage}")
        // }

        log.lifecycle("Refs:")
        git.repository.refDatabase.refs.forEach { ref ->
            log.lifecycle("  - ${ref.name}")
        }

        log.lifecycle("Tags:")
        git.tagList().call().forEach { tag ->
            log.lifecycle("  - ${tag.name}")
        }
    }

    private fun Int.toLocalDateTime(): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(this.toLong()), ZoneId.systemDefault())
    }
}
