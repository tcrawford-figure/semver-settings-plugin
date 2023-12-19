package io.github.tcrawford.versioning.specs

import io.github.tcrawford.versioning.kotest.GradleProjectsExtension
import io.github.tcrawford.versioning.kotest.shouldOnlyHave
import io.github.tcrawford.versioning.projects.RegularProject
import io.github.tcrawford.versioning.projects.SettingsProject
import io.github.tcrawford.versioning.projects.SubprojectProject
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import org.gradle.util.GradleVersion

class UninitializedRepositorySpec : FunSpec({
    val projects = install(
        GradleProjectsExtension(
            RegularProject(projectName = "regular-project"),
            SettingsProject(projectName = "settings-project"),
            SubprojectProject(projectName = "subproject-project"),
        ),
    )

    test("should build on uninitialized repository") {
        // When
        projects.build(GradleVersion.current())

        // Then
        // This assumes that the initial version is 0.0.0 and that the first build will generate a patch version
        projects.versions shouldOnlyHave "0.0.1-UNINITIALIZED-REPO"
    }
})
