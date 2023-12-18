package io.github.tcrawford.versioning.kotest

import io.github.tcrawford.versioning.projects.AbstractProject
import io.github.tcrawford.versioning.projects.GradleProjects
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

class GradleProjectsExtension(
    private vararg val abstractProjects: AbstractProject,
) : MountableExtension<Unit, GradleProjects>, AfterSpecListener, AfterTestListener {
    private lateinit var projects: GradleProjects

    override fun mount(configure: Unit.() -> Unit): GradleProjects {
        return GradleProjects.gradleProjects(projects = abstractProjects).also { projects = it }
    }

    override suspend fun afterSpec(spec: Spec) {
        projects.close()
    }

    override suspend fun afterAny(testCase: TestCase, result: TestResult) {
        projects.cleanAfterAny()
    }
}
