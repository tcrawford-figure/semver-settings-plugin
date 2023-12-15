package io.github.tcrawford.gradle.semver.projects

import com.autonomousapps.kit.AbstractGradleProject
import com.autonomousapps.kit.GradleProject
import io.github.tcrawford.gradle.semver.Constants
import io.github.tcrawford.gradle.semver.git.GitInstance
import io.github.tcrawford.gradle.semver.git.GitInstanceWriter
import io.github.tcrawford.gradle.semver.internal.command.InitializeRepo
import io.github.tcrawford.gradle.semver.internal.command.KGit
import io.github.tcrawford.gradle.semver.utils.registerForCleanup
import java.util.Properties
import kotlin.io.path.createTempDirectory

abstract class AbstractProject : AbstractGradleProject() {
    abstract val gradleProject: GradleProject
    abstract val projectName: String

    val version: String
        get() = fetchSemverProperties().getProperty("version")

    val versionTag: String
        get() = fetchSemverProperties().getProperty("versionTag")

    fun install(block: GitInstance.Builder.() -> Unit) {
        val builder = GitInstance.Builder()
        builder.block()
        install(builder.build())
    }

    fun install(gitInstance: GitInstance) {
        val remoteRepoDir = createTempDirectory("remote-repo").toFile().registerForCleanup()

        val localGit = KGit(gradleProject.rootDir, initializeRepo = InitializeRepo(bare = false, gitInstance.initialBranch))
        val remoteGit = KGit(remoteRepoDir, initializeRepo = InitializeRepo(bare = true, gitInstance.initialBranch))

        localGit.remote.add(remoteGit.git)

        val gitInstanceWriter = GitInstanceWriter(
            localGit = localGit,
            gitActionsConfig = gitInstance.actions,
        )

        gitInstanceWriter.write(gitInstance.debugging)
    }

    fun cleanGitDir() {
        gradleProject.rootDir.resolve(".git").deleteRecursively()
    }

    protected open fun fetchSemverProperties(): Properties =
        gradleProject.rootDir.resolve(Constants.SEMVER_PROPERTY_PATH)
            .let { Properties().apply { load(it.reader()) } }
}
