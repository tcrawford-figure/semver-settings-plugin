package io.github.tcrawford.versioning.projects

import com.autonomousapps.kit.AbstractGradleProject
import com.autonomousapps.kit.GradleProject
import io.github.tcrawford.versioning.Constants
import io.github.tcrawford.versioning.git.GitInstance
import io.github.tcrawford.versioning.git.GitInstanceWriter
import io.github.tcrawford.versioning.internal.command.InitializeRepo
import io.github.tcrawford.versioning.internal.command.KGit
import java.io.File
import java.util.Properties
import kotlin.io.path.createTempDirectory

abstract class AbstractProject : AbstractGradleProject(), AutoCloseable {
    abstract val gradleProject: GradleProject
    abstract val projectName: String

    private lateinit var remoteRepoDir: File

    val buildCacheDir: File =
        createTempDirectory("build-cache").toFile()

    val version: String
        get() = fetchSemverProperties().getProperty("version")

    val versionTag: String
        get() = fetchSemverProperties().getProperty("versionTag")

    fun install(block: GitInstance.Builder.() -> Unit) {
        val builder = GitInstance.Builder(this)
        builder.block()
        install(builder.build())
    }

    fun install(gitInstance: GitInstance) {
        remoteRepoDir = createTempDirectory("remote-repo").toFile()

        val localGit = KGit(gradleProject.rootDir, initializeRepo = InitializeRepo(bare = false, gitInstance.initialBranch))
        val remoteGit = KGit(remoteRepoDir, initializeRepo = InitializeRepo(bare = true, gitInstance.initialBranch))

        // GHA needs this since no author is configured in the runner
        localGit.config.author("Al Gorithm", "al.gori@thm.com")

        localGit.remote.add(remoteGit.git)

        localGit.commit("Initial commit", allowEmptyCommit = true)

        val gitInstanceWriter = GitInstanceWriter(
            localGit = localGit,
            gitActionsConfig = gitInstance.actions,
        )

        gitInstanceWriter.write(gitInstance.debugging)
    }

    fun cleanAfterAny() {
        // The remote repo must be recreated for each test
        remoteRepoDir.deleteRecursively()
        gradleProject.rootDir.resolve(".git").deleteRecursively()
    }

    override fun close() {
        buildCacheDir.deleteRecursively()
    }

    protected open fun fetchSemverProperties(): Properties =
        gradleProject.rootDir.resolve(Constants.SEMVER_PROPERTY_PATH)
            .let { Properties().apply { load(it.reader()) } }
}
