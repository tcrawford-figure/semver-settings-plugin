package io.github.tcrawford.gradle.semver

import io.github.tcrawford.gradle.semver.testkit.GradleProjectListener
import io.github.tcrawford.gradle.semver.util.resolveResourceDirectory
import io.kotest.core.spec.style.FunSpec

class SemverSettingsPluginSpec : FunSpec({

    val gradleProjectListener = GradleProjectListener(resolveResourceDirectory("sample"))

    listener(gradleProjectListener)

    test("should have git dir") {
        val runner = gradleProjectListener
            .initGradleRunner()
            .withArguments(
                // "-Psemver.stage=alpha",
                "-Psemver.modifier=patch",
                "-Psemver.tagPrefix=a",
                "-Psemver.forTesting=true",
                "--stacktrace",
                "--parallel",
                "--build-cache",
                "--configuration-cache"
            )

        runner.build()
    }
})
