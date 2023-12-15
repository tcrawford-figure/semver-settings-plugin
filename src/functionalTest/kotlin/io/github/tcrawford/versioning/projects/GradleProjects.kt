package io.github.tcrawford.versioning.projects

import io.github.tcrawford.versioning.git.GitInstance
import io.github.tcrawford.versioning.gradle.build
import org.gradle.testkit.runner.BuildResult
import org.gradle.util.GradleVersion

class GradleProjects(
    private val projects: Map<String, AbstractProject>,
) : AutoCloseable {
    companion object {
        fun gradleProjects(vararg projects: AbstractProject) =
            GradleProjects(projects.associateBy { it.projectName })
    }

    val versions: List<String>
        get() = projects.values.map { it.version }

    val versionTags: List<String>
        get() = projects.values.map { it.versionTag }

    fun install(gitInstance: GitInstance) {
        projects.values.forEach { it.install(gitInstance) }
    }

    fun install(block: GitInstance.Builder.() -> Unit) {
        projects.values.forEach { it.install(block) }
    }

    fun cleanGitDir() {
        projects.values.forEach { it.cleanGitDir() }
    }

    fun build(gradleVersion: GradleVersion, vararg args: String): Map<AbstractProject, BuildResult> =
        projects.values.associateWith { project ->
            build(gradleVersion, project.gradleProject.rootDir, *args)
        }

    override fun close() {
        projects.values.forEach { it.close() }
    }
}
