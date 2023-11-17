package io.github.tcrawford.gradle.semver.testkit

import io.github.tcrawford.gradle.semver.util.GitHandler
import io.github.tcrawford.gradle.semver.util.setupProjectDirectory
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import kotlin.io.path.createTempDirectory

class GradleProjectListener(
    private val resourcesDirectory: File,
) : TestListener {
    private lateinit var projectDir: File
    private lateinit var remoteRepoDir: File
    private lateinit var buildCacheDir: File

    private lateinit var gitHandler: GitHandler

    override suspend fun beforeAny(testCase: TestCase) {
        // Create temp directories
        projectDir = createTempDirectory("gradle-project").toFile()
        buildCacheDir = createTempDirectory("build-cache").toFile()
        remoteRepoDir = createTempDirectory("remote-repo").toFile()
        projectDir.setupProjectDirectory(buildCacheDir, resourcesDirectory)
    }

    override suspend fun afterAny(testCase: TestCase, result: TestResult) {
        // Clean up the JGit repositories
        gitHandler.close()
        // Clean up the temporary directories
        projectDir.deleteRecursively()
        remoteRepoDir.deleteRecursively()
        buildCacheDir.deleteRecursively()
    }

    fun initGradleRunner(): GradleRunner =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withProjectDir(projectDir)

    fun initRepository(config: RepositoryConfig): GradleProjectListener {
        gitHandler = GitHandler(projectDir, remoteRepoDir, config.initialBranch).apply {
            config.actions.forEach { action ->
                when (action) {
                    is CheckoutAction -> checkout(action.branch)

                    is CommitAction -> {
                        if (action.message.isNotBlank()) {
                            commit(action.message, true)
                        } else {
                            commit()
                        }

                        if (action.tag.isNotBlank()) {
                            tag(action.tag)
                        }
                    }
                }
            }

            pushAll()
            logLocalGitObjects()
        }
        return this
    }
}
