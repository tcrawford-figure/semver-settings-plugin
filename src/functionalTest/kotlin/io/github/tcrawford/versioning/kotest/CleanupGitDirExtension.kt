package io.github.tcrawford.versioning.kotest

import io.github.tcrawford.versioning.projects.GradleProjects
import io.kotest.core.listeners.AfterEachListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

class CleanupGitDirExtension(
    private val projects: GradleProjects,
) : AfterEachListener {
    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        projects.cleanGitDir()
    }
}
