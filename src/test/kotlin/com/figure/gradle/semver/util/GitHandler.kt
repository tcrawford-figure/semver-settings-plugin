package com.figure.gradle.semver.util

import com.figure.gradle.semver.log
import io.github.z4kn4fein.semver.Inc
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.inc
import io.github.z4kn4fein.semver.toVersion
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.transport.URIish
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GitHandler(
    private val localRepoDir: File,
    private val remoteRepoDir: File,
    private var startingTag: Version = "1.0.0".toVersion(),
) {
    private val localGit: Git by lazy {
        Git.init().setDirectory(localRepoDir).call()
    }

    private val remoteGit: Git by lazy {
        Git.init().setBare(true).setDirectory(remoteRepoDir).call()
    }

    init {
        localGit.remoteAdd()
            .setName("origin")
            .setUri(URIish(remoteGit.repository.directory.toURI().toURL()))
            .call()
    }

    fun createCommits(numberOfCommits: Int): GitHandler {
        repeat(numberOfCommits) { commit() }
        return this
    }

    fun createCommitsWithTags(numberOfCommits: Int, incrementBy: Inc = Inc.MINOR): GitHandler {
        repeat(numberOfCommits) {
            startingTag = startingTag.inc(incrementBy)

            val commit = commit()
            tag(startingTag, commit)
        }
        return this
    }

    fun pushAll(): GitHandler {
        localGit.push()
            .setRemote("origin")
            .setPushAll()
            .setPushTags()
            .call()
        return this
    }

    fun createBranch(branchName: String): GitHandler {
        localGit.checkout()
            .setCreateBranch(true)
            .setName(branchName)
            .call()
        return this
    }

    fun logLocalGitObjects(): GitHandler {
        logGitObjects(localGit)
        return this
    }

    fun logRemoteGitObjects(): GitHandler {
        logGitObjects(remoteGit)
        return this
    }

    fun close() {
        localGit.close()
        remoteGit.close()
    }

    private fun tag(semver: Version, commit: RevCommit) {
        localGit.tag()
            .setName("v${semver}")
            .setObjectId(commit)
            .call()
    }

    private fun commit(): RevCommit =
        localGit.commit()
            .setAllowEmpty(true)
            .setMessage("Empty commit")
            .setCommitter("Anita Bath", "anita.splash@example.com")
            .call()

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
