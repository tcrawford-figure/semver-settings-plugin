package io.github.tcrawford.gradle.semver.internal.command

import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val log = Logging.getLogger(Logger.ROOT_LOGGER_NAME)

class Print(
    private val kgit: KGit,
) {
    fun tags(debug: Boolean) {
        val logLevel = if (debug) LogLevel.LIFECYCLE else LogLevel.INFO
        log.log(logLevel, "Tags:")
        kgit.tags().forEach { tag ->
            log.log(logLevel, "  ${tag.name}")
        }
    }

    fun refs(debug: Boolean) {
        val logLevel = if (debug) LogLevel.LIFECYCLE else LogLevel.INFO
        log.log(logLevel, "Refs:")
        kgit.git.repository.refDatabase.refs.forEach { ref ->
            log.log(logLevel, "  ${ref.name}")
        }
    }

    fun commits(debug: Boolean) {
        val logLevel = if (debug) LogLevel.LIFECYCLE else LogLevel.INFO
        log.log(logLevel, "Commits:")
        kgit.log().forEach { commit ->
            log.log(logLevel, "  ${commit.name.take(8)} ${convertEpochToCustomFormat(commit.commitTime)} ${commit.shortMessage}")
        }
    }

    private fun convertEpochToCustomFormat(epochTime: Int): String {
        val instant = Instant.ofEpochSecond(epochTime.toLong())
        val formatter = DateTimeFormatter.ofPattern("MM-dd-yy HH:mm:ss").withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }
}
