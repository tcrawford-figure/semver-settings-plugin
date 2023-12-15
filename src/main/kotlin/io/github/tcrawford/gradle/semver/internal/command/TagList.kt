package io.github.tcrawford.gradle.semver.internal.command

import io.github.tcrawford.gradle.semver.internal.extensions.isNotPreRelease
import io.github.tcrawford.gradle.semver.internal.properties.Stage
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.toVersion
import io.github.z4kn4fein.semver.toVersionOrNull
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Ref

class TagList(
    private val git: Git,
) {
    operator fun invoke(): List<Ref> =
        git.tagList().call()

    fun find(tagName: String): Ref? =
        invoke().find { it.name == tagName }

    private val versionedTags: List<Version>
        get() = invoke().mapNotNull { it.name.replace(Constants.R_TAGS, "").toVersionOrNull(strict = false) }

    private val latest: Version?
        get() {
            val stages = Stage.entries.map { stage -> stage.value.lowercase() }

            return versionedTags
                // Get only stable and staged pre-releases
                .filter { version ->
                    val prereleaseLabel = version.preRelease?.substringBefore(".")?.lowercase()
                    version.isNotPreRelease || prereleaseLabel in stages
                }
                .maxOrNull()
        }

    fun latestOrInitial(initial: String): Version =
        latest ?: initial.toVersion()

    private val latestNonPreRelease: Version?
        get() = versionedTags
            .filter { version -> version.isNotPreRelease }
            .maxOrNull()

    fun latestNonPreReleaseOrInitial(initial: String): Version =
        latestNonPreRelease ?: initial.toVersion()
}
