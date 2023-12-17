package io.github.tcrawford.versioning.kotest

import io.github.tcrawford.versioning.projects.AbstractProject
import io.github.tcrawford.versioning.projects.GradleProjects
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

class GradleProjectsConfig {
    val projects: MutableList<AbstractProject> = mutableListOf()
}

object GradleProjectsContainer : MountableExtension<GradleProjectsConfig, GradleProjects>, AfterSpecListener, AfterTestListener {
    private lateinit var projects: GradleProjects

    override fun mount(configure: GradleProjectsConfig.() -> Unit): GradleProjects {
        val config = GradleProjectsConfig()
        config.configure()
        return GradleProjects.gradleProjects(*config.projects.toTypedArray()).also { this.projects = it }
    }

    override suspend fun afterAny(testCase: TestCase, result: TestResult) {
        projects.cleanGitDir()
    }

    override suspend fun afterSpec(spec: Spec) {
        projects.close()
    }
}
