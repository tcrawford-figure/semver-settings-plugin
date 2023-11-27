package io.github.tcrawford.gradle.semver.util

import io.github.tcrawford.gradle.semver.internal.command.InitializeRepo
import io.github.tcrawford.gradle.semver.internal.command.KGit
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val log: Logger = Logging.getLogger(Logger.ROOT_LOGGER_NAME)

internal class GitHandler(
    localRepoDir: File,
    remoteRepoDir: File,
    initialBranch: String
) {
    val localKGit = KGit(localRepoDir, initializeRepo = InitializeRepo(bare = false, initialBranch))
    private val remoteKGit = KGit(remoteRepoDir, initializeRepo = InitializeRepo(bare = true, initialBranch))

    init {
        localKGit.config.author("Anita Bath", "anita@spla.sh")
        localKGit.remote.add(remoteKGit.git)
    }

    fun commit(message: String = "Empty Commit", allowEmptyCommit: Boolean = false): GitHandler {
        localKGit.commit(message, allowEmptyCommit = allowEmptyCommit)
        return this
    }

    fun tag(tag: String): GitHandler {
        localKGit.tag("v$tag")
        return this
    }

    fun checkout(branch: String): GitHandler {
        if (localKGit.branch.headRef?.objectId?.name == null) {
            localKGit.commit("Initial commit", allowEmptyCommit = true)
        }

        if (localKGit.branches.exists(branch)) {
            localKGit.checkout(branch)
        } else {
            localKGit.checkout(branch, createBranch = true)
        }
        return this
    }

    fun runScript(script: File, arguments: List<String>): GitHandler {
        val processBuilder = ProcessBuilder()
        processBuilder.command("sh", script.absolutePath, *arguments.toTypedArray())
        processBuilder.redirectErrorStream(true)
        val process = processBuilder.start()
        process.waitFor()

        return this
    }

    fun pushAll(): GitHandler {
        localKGit.push.all()
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

    private fun convertEpochToCustomFormat(epochTime: Int): String {
        val instant = Instant.ofEpochSecond(epochTime.toLong())
        val formatter = DateTimeFormatter.ofPattern("MM-dd-yy HH:mm:ss").withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }

    private fun logGitObjects(git: Git) {
        println("Commits:")
        git.log().call().forEach { commit: RevCommit ->
            println("  ${commit.name.take(8)} ${convertEpochToCustomFormat(commit.commitTime)} ${commit.shortMessage}")
        }

        log.lifecycle("Refs:")
        git.repository.refDatabase.refs.forEach { ref ->
            println("  ${ref.name}")
        }

        println("Tags:")
        git.tagList().call().forEach { tag ->
            println("  ${tag.name}")
        }
    }
}
