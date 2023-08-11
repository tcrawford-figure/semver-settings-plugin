package com.figure.gradle.semver.testkit

import com.figure.gradle.semver.util.GitHandler
import com.figure.gradle.semver.util.setupProjectDirectory
import io.github.z4kn4fein.semver.toVersion
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
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

    private val numberOfCommits = 3 // Number of commits to create

    override suspend fun beforeSpec(spec: Spec) {
        initProjectDirectory()
        initGitHandler()
    }

    override suspend fun afterSpec(spec: Spec) {
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

    private fun initProjectDirectory() {
        // Create temp directories
        projectDir = createTempDirectory("gradle-project").toFile()
        buildCacheDir = createTempDirectory("build-cache").toFile()
        remoteRepoDir = createTempDirectory("remote-repo").toFile()

        projectDir.setupProjectDirectory(buildCacheDir, resourcesDirectory)
    }

    private fun initGitHandler() {
        // Initialize the Git repositories and handler
        gitHandler = GitHandler(projectDir, remoteRepoDir, "1.0.0".toVersion()).apply {
            // Create commits with tags off of `main`
            createCommitsWithTags(numberOfCommits)

            createBranch("develop")
            createCommits(numberOfCommits)

            pushAll()

            logLocalGitObjects()
        }
    }
}

