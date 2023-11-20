package io.github.tcrawford.gradle.semver.internal.command

import io.github.tcrawford.gradle.semver.internal.Stage
import io.github.tcrawford.gradle.semver.internal.extensions.isNotPreRelease
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.toVersion
import io.github.z4kn4fein.semver.toVersionOrNull
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref
import org.gradle.api.provider.Property

internal class TagList(
    private val git: Git,
) {
    operator fun invoke(): List<Ref> =
        git.tagList().call()

    fun find(tagName: String): Ref? =
        invoke().find { it.name == tagName }

    private val versionedTags: List<Version>
        get() = invoke().mapNotNull { it.name.replace("refs/tags/", "").toVersionOrNull(strict = false) }

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

    fun latestOrInitial(initial: Property<String>): Version =
        latest ?: initial.get().toVersion()

    private val latestNonPreRelease: Version?
        get() = versionedTags
            .filter { version -> version.isNotPreRelease }
            .maxOrNull()

    fun latestNonPreReleaseOrInitial(initial: Property<String>): Version =
        latestNonPreRelease ?: initial.get().toVersion()
}
